/*
 * Copyright 2015 David R. Bild
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scintuit.contrib.http4s

import java.io.FileInputStream
import java.security.KeyStore
import java.security.KeyStore.{PasswordProtection, PrivateKeyEntry}

import com.github.nscala_time.time.Imports._
import org.http4s.{Response, Request}
import org.http4s.client.Client
import scintuit.contrib.http4s.executor.Http4SExecutor
import scintuit.data.api.account._
import scintuit.data.api.login._
import scintuit.data.api.position._
import scintuit.data.api.transaction._
import scintuit.api

import scintuit.util.auth._
import scintuit.util.cache.ExpiringLruCache
import scintuit.util.transactor.Transactor
import scintuit.util.oauth._
import scintuit.util.saml._
import scintuit.contrib.play.util._

import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  val keyStoreFile = "/home/drbild/Documents/certs/tellur/intuit/tellur-dev-1/tellur-dev-1.jks"
  val keyPassword = "_yaBq8ENdn8de2sfGg5_a_xQd"
  val certAlias = "tellur-dev-1"
  val provider = SamlIssuer("tellurdev1.316564.cc.dev-intuit.ipp.prod")
  val consumer = OAuthConsumer("qyprdrdndE3OL1pPkKBFAOinL2lj5P", "UA9DoxuqKNBpC9ElJtmlVdB2QiJfZrHJlEbl7RYe")

  val key = {
    val keyStoreStream = new FileInputStream(keyStoreFile)
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
    keyStore.load(keyStoreStream, keyPassword.toCharArray)

    val entry = keyStore.getEntry(certAlias, new PasswordProtection(keyPassword.toCharArray))
      .asInstanceOf[PrivateKeyEntry]
    entry.getPrivateKey
  }

  val config = AuthConfig(key, provider, consumer)

  import api.customer._

  implicit val appInst: Applicative[CustomerIO] = MonadCustomerIO

  val clean: CustomerIO[Int] = for {
    acts <- accounts
    _    <- acts map (_.id) traverseU deleteAccount
  } yield acts.size

  val addBMO: CustomerIO[LoginError \/ Vector[Account]] =
    addLogin(11761, Seq(Credentials("j_username", "drbild"), Credentials("password", "8xumdmaJHs5_MtY82FUcvTHZJ"))) map (_ map (_.accounts))

  def mfa(session: ChallengeSession): CustomerIO[LoginError \/ Vector[Account]] =
    addLogin(100000, session.session, session.node, session.challenges map (_ => "answer")) map (_ map (_.accounts))
  
  def txs: CustomerIO[Vector[Transaction]] =
   for {
     acts <- accounts
     txs  <- acts traverseU (transactions(_, DateTime.lastMonth))
   } yield txs.flatten

  def pos: CustomerIO[Vector[Position]] =
    for {
      acts <- accounts
      pos <- acts traverseU positions
    } yield pos.flatten

  type App[A] = EitherT[CustomerIO, Exception, A]

  val client = org.http4s.client.blaze.defaultClient

  def logging(client: Client) = new Client {
    override def shutdown(): Task[Unit] = client.shutdown()

    override def prepare(req: Request): Task[Response] = for {
      start <- Task.delay(System.nanoTime())
      response <- client.prepare(req)
      stop <- Task.delay(System.nanoTime())
      elapsedNano = stop - start
      elapsedMilli = elapsedNano / 1000000
      _ = println(s"Intuit Request. ${req.method} ${req.uri} ${response.status.code} ${elapsedMilli}")
    } yield response
  }

  val cache = ExpiringLruCache[Task, String, OAuthToken](5000, DurationInt(10).minute)
  val execute = new Http4SExecutor(logging(client))
  val transactor = new Transactor(config, cache, execute)

  //val customer = "test"
  val customer = "4564f008-ad70-4984-9465-a45037faa5c1"
  val bmoAccount = 400157812572L
  val slfcuChecking = 400157901687L
  val slfcuVisa = 400157901686L
  val ingDirect = 400158247254L

  val inst = institution(11761) //slfcu


  val w = transactor.trans(customer).apply(addBMO)
  val x = transactor.trans(customer).apply(accounts)
  val y = transactor.trans(customer).apply(account(slfcuVisa))
  val z = transactor.trans(customer).apply(transactions(bmoAccount, DateTime.now minusDays 1, DateTime.now plusDays 1))

  val a = transactor.trans(customer).apply(transactions(bmoAccount, DateTime.now minusDays 3))
  val b = transactor.trans(customer).apply(updateAccount(slfcuChecking, BankingAccountType.Checking))
  val c = transactor.trans(customer).apply(accounts flatMap (_ traverseU account))

  val allPositions = accounts flatMap (_ traverseU positions)
  val d = transactor.trans(customer).apply(allPositions)

  val k = transactor.trans(customer).apply(accounts) flatMap { as =>
    Task.gatherUnordered(as map (a => transactor.trans(customer).apply(account(a.id))))
  }

  println(w.run)
//  x.run
//  println(y.run)
//  println(z.run)
//  println(a.run)
//  b.run
//  c.run
//  println(d.run)
//  k.run

  val customers = Vector(
    "0b2d604f-6c8f-4cb9-9826-8907947cb4eb",
    "cf285a03-96a5-4806-92d9-f8d295410896",
    "17e2fee8-7520-4b99-9ef6-81fe7d5fb121",
    "68b882e2-6ca2-4462-9209-6bd0f60ba7cb",
    "34f7e6ac-1513-438d-a446-ee778544e9db",
    "4c5d67a5-8a4b-4ac1-8a71-2325c5c98f46"
  )

  def deleteCustomer(cust: String) =
    transactor.trans(cust).apply(delete)

  val deleteCustomers = customers traverseU deleteCustomer
  //println(deleteCustomers.run)

}
