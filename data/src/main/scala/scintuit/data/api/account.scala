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

package scintuit.data.api

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._

import scintuit.data.api.error._
import scintuit.data.api.institution._
import scintuit.data.api.login._
import scintuit.data.raw

import scalaz.std.option._
import scalaz.syntax.apply._

/**
 * Module for account resources
 */
object account {

  type AccountId = raw.account.AccountId

  type AccountStatus = raw.account.AccountStatus
  val AccountStatus = raw.account.AccountStatus

  // ============================ Loan Property Types ============================
  type LoanTermType = raw.account.LoanTermType
  type PaymentType = raw.account.PaymentType
  type RateType = raw.account.RateType

  val LoanTermType = raw.account.LoanTermType
  val PaymentType = raw.account.PaymentType
  val RateType = raw.account.RateType

  // ================================ Account Types ================================
  type AccountType = raw.account.AccountType
  type BankingAccountType = raw.account.BankingAccountType
  type CreditAccountType = raw.account.CreditAccountType
  type InvestmentAccountType = raw.account.InvestmentAccountType
  type LoanAccountType = raw.account.LoanAccountType
  type RewardAccountType = raw.account.RewardAccountType

  val BankingAccountType = raw.account.BankingAccountType
  val CreditAccountType = raw.account.CreditAccountType
  val InvestmentAccountType = raw.account.InvestmentAccountType
  val LoanAccountType = raw.account.LoanAccountType
  val RewardAccountType = raw.account.RewardAccountType

  // ================================ Accounts ================================
  type RawAccount = raw.account.Account
  type RawOtherAccount = raw.account.OtherAccount
  type RawBankingAccount = raw.account.BankingAccount
  type RawCreditAccount = raw.account.CreditAccount
  type RawInvestmentAccount = raw.account.InvestmentAccount
  type RawLoanAccount = raw.account.LoanAccount
  type RawRewardAccount = raw.account.RewardAccount

  sealed trait Account {
    val raw: RawAccount

    private[data] def toMoney(amount: Option[BigDecimal]): Option[BigMoney] =
      (raw.currencyCode |@| amount)(BigMoney.of)

    def id: AccountId = raw.accountId
    def institutionId: InstitutionId = raw.institutionId
    def loginId: LoginId = raw.institutionLoginId
    def number: String = raw.accountNumber
    def accountType: Option[AccountType]

    def nickname: Option[String] = raw.accountNickname

    def description: Option[String] = raw.description
    def currency: Option[CurrencyUnit] = raw.currencyCode

    def balance: Option[BigMoney] = toMoney(raw.balanceAmount)
    def balanceDate: Option[DateTime] = raw.balanceDate
    def balancePrevious: Option[BigMoney] = toMoney(raw.balancePreviousAmount)

    def lastTransactionDate: Option[DateTime] = raw.lastTxnDate

    def status: AccountStatus = raw.status
    def displayPosition: Option[Int] = raw.displayPosition

    def aggregationStatus: Option[ErrorCode] = raw.aggrStatusCode
    def aggregationAttemptDate: Option[DateTime] = raw.aggrAttemptDate
    def aggregationSuccessDate: Option[DateTime] = raw.aggrSuccessDate

    def maskNumber(f: String => String): Account
  }

  object Account {
    def apply(raw: RawAccount): Account = raw match {
      case a: RawOtherAccount => OtherAccount(a)
      case a: RawBankingAccount => BankingAccount(a)
      case a: RawCreditAccount => CreditAccount(a)
      case a: RawInvestmentAccount => InvestmentAccount(a)
      case a: RawLoanAccount => LoanAccount(a)
      case a: RawRewardAccount => RewardAccount(a)
    }
  }

  case class OtherAccount(raw: RawOtherAccount) extends Account {
    override def accountType: Option[AccountType] = None

    override def maskNumber(f: (String) => String): OtherAccount =
      OtherAccount(raw.copy(accountNumber = f(raw.accountNumber)))
  }

  case class BankingAccount(raw: RawBankingAccount) extends Account {
    def accountType: Option[BankingAccountType] = raw.bankingAccountType
    def openDate: Option[DateTime] = raw.openDate
    def originationDate: Option[DateTime] = raw.originationDate

    def balanceAvailable: Option[BigMoney] = toMoney(raw.availableBalanceAmount)

    def depositPeriod: Option[BigMoney] = toMoney(raw.periodDepositAmount)

    def interestType: Option[String] = raw.interestType
    def interestRatePeriod: Option[BigDecimal] = raw.periodInterestRate
    def interestPeriod: Option[BigMoney] = toMoney(raw.periodInterestAmount)
    def interestYTD: Option[BigMoney] = toMoney(raw.interestAmountYtd)
    def interestPriorYTD: Option[BigMoney] = toMoney(raw.interestPriorAmountYtd)

    def maturityDate: Option[DateTime] = raw.maturityDate
    def maturityAmount: Option[BigMoney] = toMoney(raw.maturityAmount)

    def transactionsRefreshDate: Option[DateTime] = raw.postedDate

    def maskNumber(f: String => String): BankingAccount =
      BankingAccount(raw.copy(accountNumber = f(raw.accountNumber)))
  }

  case class CreditAccount(raw: RawCreditAccount) extends Account {
    def accountType: Option[CreditAccountType] = raw.creditAccountType
    def descriptionDetails: Option[String] = raw.detailedDescription

    override def balance: Option[BigMoney] = super.balance orElse creditBalance

    def creditBalance: Option[BigMoney] = toMoney(raw.currentBalance)
    def creditBalancePrevious: Option[BigMoney] = toMoney(raw.balancePreviousAmount)
    def creditAvailable: Option[BigMoney] = toMoney(raw.creditAvailableAmount)
    def creditMax: Option[BigMoney] = toMoney(raw.creditMaxAmount)
    def creditRate: Option[BigDecimal] = raw.interestRate

    def cashAdvanceBalance: Option[BigMoney] = toMoney(raw.cashAdvanceBalance)
    def cashAdvanceAvailable: Option[BigMoney] = toMoney(raw.cashAdvanceAvailableAmount)
    def cashAdvanceMax: Option[BigMoney] = toMoney(raw.cashAdvanceMaxAmount)
    def cashAdvanceRate: Option[BigDecimal] = raw.cashAdvanceInterestRate

    def paymentMin: Option[BigMoney] = toMoney(raw.paymentMinAmount)
    def paymentDueDate: Option[DateTime] = raw.paymentDueDate
    def paymentPrevious: Option[BigMoney] = toMoney(raw.lastPaymentAmount)
    def paymentPreviousDate: Option[DateTime] = raw.lastPaymentDate

    def pastDueAmount: Option[BigMoney] = toMoney(raw.pastDueAmount)

    def statementEndDate: Option[DateTime] = raw.statementEndDate
    def statementCloseBalance: Option[BigMoney] = toMoney(raw.statementCloseBalance)
    def statementPurchaseAmount: Option[BigMoney] = toMoney(raw.statementPurchaseAmount)
    def statementFinanceAmount: Option[BigMoney] = toMoney(raw.statementFinanceAmount)
    def statementLateFeeAmount: Option[BigMoney] = toMoney(raw.statementLateFeeAmount)

    def maskNumber(f: String => String): CreditAccount =
      CreditAccount(raw.copy(accountNumber = f(raw.accountNumber)))
  }

  case class InvestmentAccount(raw: RawInvestmentAccount) extends Account {
    def accountType: Option[InvestmentAccountType] = raw.investmentAccountType

    override def balance: Option[BigMoney] = super.balance orElse investmentBalance

    def cashBalance: Option[BigMoney] = toMoney(raw.cashBalanceAmount)
    def cashBalanceAvailable: Option[BigMoney] = toMoney(raw.availableCashBalance)

    def interestMarginBalance: Option[BigMoney] = toMoney(raw.interestMarginBalance)
    def investmentBalance: Option[BigMoney] = toMoney(raw.currentBalance)
    def shortBalance: Option[BigMoney] = toMoney(raw.shortBalance)
    def unvestedBalance: Option[BigMoney] = toMoney(raw.unvestedBalance)
    def vestedBalance: Option[BigMoney] = toMoney(raw.vestedBalance)

    def employerMatch: Option[BigMoney] = toMoney(raw.empMatchAmount)
    def employerMatchYTD: Option[BigMoney] = toMoney(raw.empMatchAmountYtd)
    def employerMatchDeferred: Option[BigMoney] = toMoney(raw.empMatchDeferAmount)
    def employerMatchDeferredYTD: Option[BigMoney] = toMoney(raw.empMatchDeferAmountYtd)
    def employerPretaxContribution: Option[BigMoney] = toMoney(raw.empPretaxContribAmount)
    def employerPretaxContributionYTD: Option[BigMoney] = toMoney(raw.empPretaxContribAmountYtd)

    def loanBalance: Option[BigMoney] = toMoney(raw.currentLoanBalance)
    def loanBalanceInitial: Option[BigMoney] = toMoney(raw.initialLoanBalance)
    def loanRate: Option[BigDecimal] = raw.loanRate
    def loanStartDate: Option[DateTime] = raw.loanStartDate

    def maturityValue: Option[BigMoney] = toMoney(raw.maturityValueAmount)
    def rolloverInterestToDate: Option[BigMoney] = toMoney(raw.rolloverItd)

    def maskNumber(f: String => String): InvestmentAccount =
      InvestmentAccount(raw.copy(accountNumber = f(raw.accountNumber)))
  }

  case class LoanAccount(raw: RawLoanAccount) extends Account {
    def accountType: Option[LoanAccountType] = raw.loanType
    def descriptionDetails: Option[String] = raw.loanDescription
    def referenceNumber: Option[String] = raw.referenceNumber

    def initialBalance: Option[BigMoney] = toMoney(raw.initialAmount)
    def initialDate: Option[DateTime] = raw.initialDate
    def initialMaturityDate: Option[DateTime] = raw.originalMaturityDate

    def escrowBalance: Option[BigMoney] = toMoney(raw.escrowBalance)

    def loanBalance: Option[BigMoney] = toMoney(raw.endingBalanceAmount)
    def loanBalanceAvailable: Option[BigMoney] = toMoney(raw.availableBalanceAmount)
    def loanTermType: Option[LoanTermType] = raw.loanTermType

    def principalBalance: Option[BigMoney] = toMoney(raw.principalBalance)
    def principalPaidYTD: Option[BigMoney] = toMoney(raw.principalPaidYTD)

    def interestPaidLTD: Option[BigMoney] = toMoney(raw.interestPaidLtd)
    def interestPaidYTD: Option[BigMoney] = toMoney(raw.interestPaidYTD)
    def interestProjected: Option[BigMoney] = toMoney(raw.projectedInterest)
    def interestPeriod: Option[String] = raw.interestPeriod
    def interestRate: Option[BigDecimal] = raw.interestRate
    def interestRateType: Option[RateType] = raw.interestRateType

    def insurancePaidYTD: Option[BigMoney] = toMoney(raw.insurancePaidYTD)
    def taxPaidYTD: Option[BigMoney] = toMoney(raw.taxPaidYTD)

    def payment: Option[BigMoney] = toMoney(raw.nextPayment)
    def paymentMin: Option[BigMoney] = toMoney(raw.paymentMinAmount)
    def paymentPrincipal: Option[BigMoney] = toMoney(raw.nextPaymentPrincipalAmount)
    def paymentInterest: Option[BigMoney] = toMoney(raw.nextPaymentInterestAmount)
    def paymentDueDate: Option[DateTime] = raw.nextPaymentDate

    def paymentAuto: Option[Boolean] = raw.autoPayEnrolled
    def paymentFirstDate: Option[DateTime] = raw.firstPaymentDate
    def paymentFrequency: Option[String] = raw.loanPaymentFreq
    def paymentType: Option[PaymentType] = raw.loanPaymentType
    def paymentRecurring: Option[BigMoney] = toMoney(raw.recurringPaymentAmount)

    def paymentPrevious: Option[BigMoney] = toMoney(raw.lastPaymentAmount)
    def paymentPreviousPrincipal: Option[BigMoney] = toMoney(raw.lastPaymentPrincipalAmount)
    def paymentPreviousInterest: Option[BigMoney] = toMoney(raw.lastPaymentInterestAmount)
    def paymentPreviousEscrow: Option[BigMoney] = toMoney(raw.lastPaymentEscrowAmount)
    def paymentPreviousFee: Option[BigMoney] = toMoney(raw.lastPaymentLastFeeAmount)
    def paymentPreviousLateFee: Option[BigMoney] = toMoney(raw.lastPaymentLateCharge)
    def paymentPreviousDueDate: Option[DateTime] = raw.lastPaymentDueDate
    def paymentPreviousDate: Option[DateTime] = raw.lastPaymentReceiveDate

    def paymentsApplied: Option[Int] = raw.noOfPayments
    def paymentsRemaining: Option[Int] = raw.remainingPayments

    def payoff: Option[BigMoney] = toMoney(raw.payoffAmount)
    def payoffDate: Option[DateTime] = raw.payoffAmountDate

    def balloon: Option[BigMoney] = toMoney(raw.balloonAmount)
    def lateFee: Option[BigMoney] = toMoney(raw.lateFeeAmount)

    def schoolCurrent: Option[String] = raw.currentSchool
    def schoolOriginal: Option[String] = raw.originalSchool

    def collateral: Option[String] = raw.collateral
    def guarantor: Option[String] = raw.guarantor
    def lender: Option[String] = raw.lender
    def term: Option[String] = raw.term
    def taxPayee: Option[String] = raw.taxPayeeName
    def firstMortgage: Option[Boolean] = raw.firstMortgage

    def transactionsRefreshDate: Option[DateTime] = raw.postedDate

    def maskNumber(f: String => String): LoanAccount =
      LoanAccount(raw.copy(accountNumber = f(raw.accountNumber)))
  }

  case class RewardAccount(raw: RawRewardAccount) extends Account {
    def accountType: Option[RewardAccountType] = raw.programType

    def rewardBalance: Option[BigDecimal] = raw.currentBalance
    def rewardBalanceOriginal: Option[BigDecimal] = raw.originalBalance
    def rewardQualifyYTD: Option[BigDecimal] = raw.rewardQualifyAmountYtd
    def rewardEarnedLifetime: Option[BigDecimal] = raw.rewardLifetimeEarned

    def transactionsRefeshDate: Option[DateTime] = raw.postedDate

    def maskNumber(f: String => String): RewardAccount =
      RewardAccount(raw.copy(accountNumber = f(raw.accountNumber)))
  }
}
