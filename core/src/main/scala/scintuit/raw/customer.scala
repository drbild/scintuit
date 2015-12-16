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

package scintuit.raw

import com.github.nscala_time.time.Imports._

import scintuit.data.raw.account._
import scintuit.data.raw.institution._
import scintuit.data.raw.login._
import scintuit.data.raw.position._
import scintuit.data.raw.transaction._

import scalaz.{Coyoneda, Monad, Free, \/}


object customer {

  /**
   * Typeclass for representations of an Intuit customer
   */
  trait Customer[T] {
    def name(customer: T): String
  }

  object Customer {
    def apply[C: Customer]: Customer[C] = implicitly[Customer[C]]

    implicit object StringCustomer extends Customer[String] {
      def name(customer: String): String = customer
    }
  }

  /**
   * ADT for Intuit API operations
   */
  sealed trait CustomerOp[A]

  object CustomerOp {
    // ---------------------------- Institutions ----------------------------
    case object ListInstitutions extends CustomerOp[Vector[Institution]] {
      override def toString = s"listInstitutions"
    }

    case class GetInstitution(id: InstitutionId) extends CustomerOp[Option[InstitutionDetails]] {
      override def toString = s"getInstitution (institutionId=$id)"
    }

    // ------------------------------- Logins -------------------------------
    case class AddAccounts(
      id: InstitutionId,
      credentials: Seq[Credentials]
    ) extends CustomerOp[LoginError \/ Vector[Account]] {
      override def toString = s"addAccounts (institutionId=$id)"
    }

    case class AddAccountsChallenge(
      id: InstitutionId,
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]
    ) extends CustomerOp[LoginError \/ Vector[Account]] {
      override def toString = s"addAccountsChallenge (institutionId=$id)"
    }

    case class UpdateLogin(
      id: LoginId,
      credentials: Seq[Credentials]
    ) extends CustomerOp[LoginError \/ Unit] {
      override def toString = s"updateLogin (loginId=$id)"
    }

    case class UpdateLoginChallenge(
      id: LoginId,
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]
    ) extends CustomerOp[LoginError \/ Unit] {
      override def toString = s"updateLoginChallenge (loginId=$id)"
    }

    // ------------------------------ Accounts ------------------------------
    case class GetAccount(id: AccountId) extends CustomerOp[Account] {
      override def toString = s"getAccount (accountId=$id)"
    }

    case class DeleteAccount(id: AccountId) extends CustomerOp[Int] {
      override def toString = s"deleteAccount (accountId=$id)"

    }

    case object ListCustomerAccounts extends CustomerOp[Vector[Account]] {
      override def toString = s"listCustomerAccounts"
    }

    case class ListLoginAccounts(id: LoginId) extends CustomerOp[Vector[Account]] {
      override def toString = s"listLoginAccounts (loginId=$id)"
    }

    case class UpdateAccountType(id: AccountId, accountType: AccountType) extends CustomerOp[Unit] {
      override def toString = s"updateAccountType (accountId=$id)"
    }

    // ---------------------------- Transactions ----------------------------
    case class ListTransactions(
      id: AccountId,
      start: DateTime,
      end: Option[DateTime]
    ) extends CustomerOp[TransactionsResponse] {
      override def toString = s"listTransactions (accountId=$id, start=$start, end=${end getOrElse "<none>"})"
    }

    // ------------------------------ Positions -----------------------------
    case class ListPositions(id: AccountId) extends CustomerOp[Vector[Position]] {
      override def toString = s"listPositions (accountId=$id)"
    }

    // ----------------------------- Customers ------------------------------
    case object DeleteCustomer extends CustomerOp[Int] {
      override def toString = s"deleteCustomer"
    }
  }

  import CustomerOp._

  /**
   * Free monad over a free functor of [[CustomerOp]].
   */
  type CustomerIO[A] = Free.FreeC[CustomerOp, A]

  /**
   * Monad instance for [[CustomerIO]] (can't be be inferred).
   */
  implicit val MonadCustomerIO: Monad[CustomerIO] = Free.freeMonad[Coyoneda[CustomerOp, ?]]

  // ---------------------------- Institutions ----------------------------
  val getInstitutions: CustomerIO[Vector[Institution]] =
    Free.liftFC(ListInstitutions)

  def getInstitutionDetails(id: Long): CustomerIO[Option[InstitutionDetails]] =
    Free.liftFC(GetInstitution(id))

  // ------------------------------- Logins -------------------------------
  def addAccounts(id: InstitutionId, credentials: Seq[Credentials]): CustomerIO[LoginError \/ Vector[Account]] =
    Free.liftFC(AddAccounts(id, credentials))

  def addAccounts(
    id: InstitutionId,
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]
  ): CustomerIO[LoginError \/ Vector[Account]] =
    Free.liftFC(AddAccountsChallenge(id, sessionId, nodeId, answers))

  def updateLogin(id: LoginId, credentials: Seq[Credentials]): CustomerIO[LoginError \/ Unit] =
    Free.liftFC(UpdateLogin(id, credentials))

  def updateLogin(
    id: LoginId,
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]
  ): CustomerIO[LoginError \/ Unit] =
    Free.liftFC(UpdateLoginChallenge(id, sessionId, nodeId, answers))

  // ------------------------------ Accounts ------------------------------
  def getAccount(id: AccountId): CustomerIO[Account] =
    Free.liftFC(GetAccount(id))

  val getCustomerAccounts: CustomerIO[Vector[Account]] =
    Free.liftFC(ListCustomerAccounts)

  def getLoginAccounts(id: LoginId): CustomerIO[Vector[Account]] =
    Free.liftFC(ListLoginAccounts(id))

  def deleteAccount(id: AccountId): CustomerIO[Int] =
    Free.liftFC(DeleteAccount(id))

  def updateAccountType(id: AccountId, accountType: AccountType): CustomerIO[Unit] =
    Free.liftFC(UpdateAccountType(id, accountType))

  // ------------------------------ Transactions ------------------------------
  def getTransactions(id: AccountId, start: DateTime, end: Option[DateTime]): CustomerIO[TransactionsResponse] =
    Free.liftFC(ListTransactions(id, start, end))

  // ------------------------------ Positions ------------------------------
  def getPositions(id: AccountId): CustomerIO[Vector[Position]] =
    Free.liftFC(ListPositions(id))

  // ----------------------------- Customers ------------------------------
  val delete: CustomerIO[Int] =
    Free.liftFC(DeleteCustomer)
}
