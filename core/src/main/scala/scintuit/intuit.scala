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

package scintuit

import com.github.nscala_time.time.Imports._
import scintuit.data._

import scalaz._


object intuit {
  /**
   * ADT for Intuit API operations
   */
  sealed trait IntuitOp[A]

  object IntuitOp {
    // ---------------------------- Institutions ----------------------------
    case object ListInstitutions extends IntuitOp[Vector[InstitutionSummary]] {
      override def toString = s"listInstitutions"
    }

    case class GetInstitution(id: InstitutionId) extends IntuitOp[Option[Institution]] {
      override def toString = s"getInstitution (institutionId=$id)"
    }

    // ------------------------------- Logins -------------------------------
    case class AddAccounts(
      id: InstitutionId,
      credentials: Seq[Credentials]
    ) extends IntuitOp[LoginError \/ Vector[Account]] {
      override def toString = s"addAccounts (institutionId=$id)"
    }

    case class AddAccountsChallenge(
      id: InstitutionId,
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]
    ) extends IntuitOp[LoginError \/ Vector[Account]] {
      override def toString = s"addAccountsChallenge (institutionId=$id)"
    }

    case class UpdateLogin(
      id: LoginId,
      credentials: Seq[Credentials]
    ) extends IntuitOp[LoginError \/ Unit] {
      override def toString = s"updateLogin (loginId=$id)"
    }

    case class UpdateLoginChallenge(
      id: LoginId,
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]
    ) extends IntuitOp[LoginError \/ Unit] {
      override def toString = s"updateLoginChallenge (loginId=$id)"
    }

    // ------------------------------ Accounts ------------------------------
    case class GetAccount(id: AccountId) extends IntuitOp[Account] {
      override def toString = s"getAccount (accountId=$id)"
    }

    case class DeleteAccount(id: AccountId) extends IntuitOp[Int] {
      override def toString = s"deleteAccount (accountId=$id)"

    }

    case object ListCustomerAccounts extends IntuitOp[Vector[Account]] {
      override def toString = s"listCustomerAccounts"
    }

    case class ListLoginAccounts(id: LoginId) extends IntuitOp[Vector[Account]] {
      override def toString = s"listLoginAccounts (loginId=$id)"
    }

    case class UpdateAccountType(id: AccountId, accountType: AccountType) extends IntuitOp[Unit] {
      override def toString = s"updateAccountType (accountId=$id)"
    }

    // ---------------------------- Transactions ----------------------------
    case class ListTransactions(
      id: AccountId,
      start: DateTime,
      end: Option[DateTime]
    ) extends IntuitOp[TransactionsResponse] {
      override def toString = s"listTransactions (accountId=$id, start=$start, end=${end getOrElse "<none>"})"
    }

    // ------------------------------ Positions -----------------------------
    case class ListPositions(id: AccountId) extends IntuitOp[Vector[Position]] {
      override def toString = s"listPositions (accountId=$id)"
    }

    // ----------------------------- Customers ------------------------------
    case object DeleteCustomer extends IntuitOp[Int] {
      override def toString = s"deleteCustomer"
    }
  }

  import IntuitOp._

  /**
   * Free monad over a free functor of [[IntuitOp]].
   */
  type IntuitIO[A] = Free.FreeC[IntuitOp, A]

  /**
   * Monad instance for [[IntuitIO]] (can't be be inferred).
   */
  implicit val MonadIntuitIO: Monad[IntuitIO] = Free.freeMonad[({type λ[α] = Coyoneda[IntuitOp, α]})#λ]

  // ---------------------------- Institutions ----------------------------
  val institutions: IntuitIO[Vector[InstitutionSummary]] = Free.liftFC(ListInstitutions)
  def institution(id: Long): IntuitIO[Option[Institution]] = Free.liftFC(GetInstitution(id))

  // ------------------------------- Logins -------------------------------
  def addAccounts(id: InstitutionId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ Vector[Account]] =
    Free.liftFC(AddAccounts(id, credentials))

  def addAccounts(
    id: InstitutionId,
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]): IntuitIO[LoginError \/ Vector[Account]] =
    Free.liftFC(AddAccountsChallenge(id, sessionId, nodeId, answers))

  def updateLogin(id: LoginId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ Unit] =
    Free.liftFC(UpdateLogin(id, credentials))

  def updateLogin(
    id: InstitutionId,
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]): IntuitIO[LoginError \/ Unit] =
    Free.liftFC(UpdateLoginChallenge(id, sessionId, nodeId, answers))

  // ------------------------------ Accounts ------------------------------
  def account(id: AccountId): IntuitIO[Account] = Free.liftFC(GetAccount(id))
  val accounts: IntuitIO[Vector[Account]] = Free.liftFC(ListCustomerAccounts)
  def accounts(id: LoginId): IntuitIO[Vector[Account]] = Free.liftFC(ListLoginAccounts(id))
  def deleteAccount(id: AccountId): IntuitIO[Int] = Free.liftFC(DeleteAccount(id))
  def updateAccountType(id: AccountId, accountType: AccountType): IntuitIO[Unit] = Free.liftFC(UpdateAccountType(id, accountType))

  // ------------------------------ Transactions ------------------------------
  def transactions(id: AccountId, start: DateTime) = Free.liftFC(ListTransactions(id, start, None))
  def transactions(id: AccountId, start: DateTime, end: DateTime) = Free.liftFC(ListTransactions(id, start, Some(end)))

  // ------------------------------ Positions ------------------------------
  def positions(id: AccountId): IntuitIO[Vector[Position]] = Free.liftFC(ListPositions(id))

  // ----------------------------- Customers ------------------------------
  def deleteCustomer: IntuitIO[Int] = Free.liftFC(DeleteCustomer)
}
