package scintuit.data.api

import enumeratum.{Enum, EnumEntry}
import scintuit.data.raw

import scala.language.postfixOps

/**
 * Module for categorization types
 */
object categorization {

  type CategorizationSource = raw.categorization.CategorizationSource
  val CategorizationSource = raw.categorization.CategorizationSource

  type RawCategorization = raw.categorization.Categorization
  type RawCategorizationContext = raw.categorization.CategorizationContext
  type RawCategorizationCommon = raw.categorization.CategorizationCommon

  val RawCategorization = raw.categorization.Categorization
  val RawCategorizationContext = raw.categorization.CategorizationContext
  val RawCategorizationCommon = raw.categorization.CategorizationCommon

  sealed abstract class ConsumerCategory(val code: Int, val name: String) extends EnumEntry {
    override def entryName: String = name
    def group: ConsumerCategory = this

    def isA(category: ConsumerCategory): Boolean =
      (this == category)  || (this.group == category)
  }

  sealed abstract class ConsumerSubcategory(code: Int, name: String, override val group: ConsumerCategory) extends ConsumerCategory(code, name)

  object ConsumerCategory extends Enum[ConsumerCategory] {
    val values = findValues
    
    case object Entertainment extends ConsumerCategory(1, "Entertainment")
    case object Arts extends ConsumerSubcategory(101, "Arts", Entertainment)
    case object Amusement extends ConsumerSubcategory(102, "Amusement", Entertainment)
    case object Music extends ConsumerSubcategory(103, "Music", Entertainment)
    case object MoviesAndDVDs extends ConsumerSubcategory(104, "Movies & DVDs", Entertainment)
    case object NewspapersAndMagazines extends ConsumerSubcategory(105, "Newspapers & Magazines", Entertainment)

    case object Shopping extends ConsumerCategory(2, "Shopping")
    case object Clothing extends ConsumerSubcategory(201, "Clothing", Shopping)
    case object Books extends ConsumerSubcategory(202, "Books", Shopping)
    case object ElectronicsAndSoftware extends ConsumerSubcategory(204, "Electronics & Software", Shopping)
    case object Hobbies extends ConsumerSubcategory(206, "Hobbies", Shopping)
    case object SportingGoods extends ConsumerSubcategory(207, "Sporting Goods", Shopping)

    case object PersonalCare extends ConsumerCategory(4, "Personal Care")
    case object Hair extends ConsumerSubcategory(403, "Hair", PersonalCare)
    case object SpaAndMassage extends ConsumerSubcategory(404, "Spa & Massage", PersonalCare)
    case object Laundry extends ConsumerSubcategory(406, "Laundry", PersonalCare)

    case object HealthAndFitness extends ConsumerCategory(5, "Health & Fitness")
    case object Dentist extends ConsumerSubcategory(501, "Dentist", HealthAndFitness)
    case object Docker extends ConsumerSubcategory(502, "Doctor", HealthAndFitness)
    case object Eyecare extends ConsumerSubcategory(503, "Eyecare", HealthAndFitness)
    case object Pharmacy extends ConsumerSubcategory(505, "Pharmacy", HealthAndFitness)
    case object HealthInsurance extends ConsumerSubcategory(506, "Health Insurance", HealthAndFitness)
    case object Gym extends ConsumerSubcategory(507, "Gym", HealthAndFitness)
    case object Sports extends ConsumerSubcategory(508, "Sports", HealthAndFitness)

    case object Kids extends ConsumerCategory(6, "Kids")
    case object BabysitterAndDayCare extends ConsumerSubcategory(602, "Babysitter & Daycare", Kids)
    case object ChildSupport extends ConsumerSubcategory(603, "Child Support", Kids)
    case object Toys extends ConsumerSubcategory(606, "Toys", Kids)
    case object KidsActivities extends ConsumerSubcategory(609, "Kids Activities", Kids)
    case object Allowance extends ConsumerSubcategory(610, "Allowance", Kids)
    case object BabySupplies extends ConsumerSubcategory(611, "Baby Supplies", Kids)

    case object FoodAndDining extends ConsumerCategory(7, "Food & Dining")
    case object Groceries extends ConsumerSubcategory(701, "Groceries", FoodAndDining)
    case object CoffeeShops extends ConsumerSubcategory(704, "Coffee Shops", FoodAndDining)
    case object FastFood extends ConsumerSubcategory(706, "Fast Food", FoodAndDining)
    case object Restaurants extends ConsumerSubcategory(707, "Restaurants", FoodAndDining)
    case object AlcoholAndBars extends ConsumerSubcategory(708, "Alcohol & Bars", FoodAndDining)

    case object GiftsAndDonations extends ConsumerCategory(8, "Gifts & Donations")
    case object Gift extends ConsumerSubcategory(801, "Gift", GiftsAndDonations)
    case object Charity extends ConsumerSubcategory(802, "Charity", GiftsAndDonations)

    case object Pets extends ConsumerCategory(9, "Pets")
    case object PetFoodAndSupplies extends ConsumerSubcategory(901, "Pet Food & Supplies", Pets)
    case object PetGrooming extends ConsumerSubcategory(902, "Pet Grooming", Pets)
    case object Veterinary extends ConsumerSubcategory(903, "Veterinary", Pets)

    case object Education extends ConsumerCategory(10, "Education")
    case object Tuition extends ConsumerSubcategory(1001, "Tuition", Education)
    case object StudentLoan extends ConsumerSubcategory(1002, "Student Loan", Education)
    case object BooksAndSupplies extends ConsumerSubcategory(1003, "Books & Supplies", Education)

    case object Financial extends ConsumerCategory(11, "Financial")
    case object LifeInsurance extends ConsumerSubcategory(1102, "Life Insurance", Financial)
    case object FinancialAdvisor extends ConsumerSubcategory(1105, "Financial Advisor", Financial)

    case object Home extends ConsumerCategory(12, "Home")
    case object Furnishings extends ConsumerSubcategory(1201, "Furnishings", Home)
    case object Lawn extends ConsumerSubcategory(1202, "Lawn & Garden", Home)
    case object HomeImprovement extends ConsumerSubcategory(1203, "Home Improvement", Home)
    case object HomeServices extends ConsumerSubcategory(1204, "Home Services", Home)
    case object HomeInsurance extends ConsumerSubcategory(1206, "Home Insurance", Home)
    case object MortgageAndRent extends ConsumerSubcategory(1207, "Mortgage & Rent", Home)
    case object HomeSupplies extends ConsumerSubcategory(1208, "Home Supplies", Home)

    case object BillsAndUtilities extends ConsumerCategory(13, "Bills & Utilities")
    case object Television extends ConsumerSubcategory(1301, "Television", BillsAndUtilities)
    case object HomePhone extends ConsumerSubcategory(1302, "Home Phone", BillsAndUtilities)
    case object Internet extends ConsumerSubcategory(1303, "Internet", BillsAndUtilities)
    case object MobilePhone extends ConsumerSubcategory(1304, "Mobile Phone", BillsAndUtilities)
    case object Utilities extends ConsumerSubcategory(1306, "Utilities", BillsAndUtilities)

    case object AutoAndTransport extends ConsumerCategory(14, "Auto & Transport")
    case object GasAndFuel extends ConsumerSubcategory(1401, "Gas & Fuel", AutoAndTransport)
    case object Parking extends ConsumerSubcategory(1402, "Parking", AutoAndTransport)
    case object ServiceAndParts extends ConsumerSubcategory(1403, "Service & Parts", AutoAndTransport)
    case object AutoPayment extends ConsumerSubcategory(1404, "Auto Payment", AutoAndTransport)
    case object AutoInsurance extends ConsumerSubcategory(1405, "Auto Insurance", AutoAndTransport)
    case object PublicTransportation extends ConsumerSubcategory(1406, "Public Transportation", AutoAndTransport)

    case object Travel extends ConsumerCategory(15, "Travel")
    case object AirTravel extends ConsumerSubcategory(1501, "Air Travel", Travel)
    case object Hotel extends ConsumerSubcategory(1502, "Hotel", Travel)
    case object RentalCarAndTaxi extends ConsumerSubcategory(1503, "Rental Car & Taxi", Travel)
    case object Vacation extends ConsumerSubcategory(1504, "Vacation", Travel)

    case object FeesAndCharges extends ConsumerCategory(16, "Fees & Charges")
    case object ServiceFee extends ConsumerSubcategory(1601, "Service Fee", FeesAndCharges)
    case object LateFee extends ConsumerSubcategory(1602, "Late Fee", FeesAndCharges)
    case object FinanceCharge extends ConsumerSubcategory(1604, "Finance Charge", FeesAndCharges)
    case object ATMFee extends ConsumerSubcategory(1605, "ATM Fee", FeesAndCharges)
    case object BankFee extends ConsumerSubcategory(1606, "Bank Fee", FeesAndCharges)
    case object TradeCommissions extends ConsumerSubcategory(1607, "Trade Commissions", FeesAndCharges)

    case object BusinessServices extends ConsumerCategory(17, "Business Services")
    case object Advertising extends ConsumerSubcategory(1701, "Advertising", BusinessServices)
    case object OfficeSupplies extends ConsumerSubcategory(1702, "Office Supplies", BusinessServices)
    case object Printing extends ConsumerSubcategory(1703, "Printing", BusinessServices)
    case object Shipping extends ConsumerSubcategory(1704, "Shipping", BusinessServices)
    case object Legal extends ConsumerSubcategory(1705, "Legal", BusinessServices)

    case object Taxes extends ConsumerCategory(19, "Taxes")
    case object FederalTax extends ConsumerSubcategory(1901, "Federal Tax", Taxes)
    case object StateTax extends ConsumerSubcategory(1902, "State Tax", Taxes)
    case object LocalTax extends ConsumerSubcategory(1903, "Local Tax", Taxes)
    case object SalesTax extends ConsumerSubcategory(1904, "Sales Tax", Taxes)
    case object PropertyTax extends ConsumerSubcategory(1905, "Property Tax", Taxes)

    case object Uncategorized extends ConsumerCategory(20, "Uncategorized")
    case object CashAndATM extends ConsumerSubcategory(2001, "Cash & ATM", Uncategorized)
    case object Check extends ConsumerSubcategory(2002, "Check", Uncategorized)

    case object Transfer extends ConsumerCategory(21, "Transfer")
    case object CreditCardPayment extends ConsumerSubcategory(2101, "Credit Card Payment", Transfer)
    case object TransferForCashSpending extends ConsumerSubcategory(2102, "Transfer for Cash Spending", Transfer)

    case object Income extends ConsumerCategory(30, "Income")
    case object Paycheck extends ConsumerSubcategory(3001, "Paycheck", Income)
    case object ReturnedPurchase extends ConsumerSubcategory(3003, "Returned Purchase", Income)
    case object Bonus extends ConsumerSubcategory(3004, "Bonus", Income)
    case object InterestIncome extends ConsumerSubcategory(3005, "Interest Income", Income)
    case object Reimbursement extends ConsumerSubcategory(3006, "Reimbursement", Income)
    case object RentalIncome extends ConsumerSubcategory(3007, "Rental Income", Income)

    case object Investments extends ConsumerCategory(50, "Investments")
    case object Deposit extends ConsumerSubcategory(5001, "Deposit", Investments)
    case object Withdrawal extends ConsumerSubcategory(5002, "Withdrawal", Investments)
    case object DividendAndCapGains extends ConsumerSubcategory(5003, "Dividend & Cap Gains", Investments)
    case object Buy extends ConsumerSubcategory(5004, "Buy", Investments)
    case object Sell extends ConsumerSubcategory(5005, "Sell", Investments)

    case object Loans extends ConsumerCategory(60, "Loans")
    case object LoanPayment extends ConsumerSubcategory(6001, "Loan Payment", Loans)
    case object LoanInsurance extends ConsumerSubcategory(6002, "Loan Insurance", Loans)
    case object LoanPrincipal extends ConsumerSubcategory(6003, "Loan Principal", Loans)
    case object LoanInterest extends ConsumerSubcategory(6004, "Loan Interest", Loans)
    case object LoanFeesAndCharges extends ConsumerSubcategory(6005, "Loan Fees and Charges", Loans)

  }

  case class CategorizationContext(raw: RawCategorizationContext) {
    def category: Option[ConsumerCategory] = raw.categoryName flatMap ConsumerCategory.withNameInsensitiveOption
    def scheduleC: Option[String] = raw.scheduleC

    def source: Option[CategorizationSource] = raw.source
    def contextType: Option[String] = raw.contextType
  }

  case class Categorization(raw: RawCategorization) {
    def payee: Option[String] = raw.common.normalizedPayeeName
    def merchant: Option[String] = raw.common.merchant
    def sic: Option[Int] = raw.common.sic

    def categories: Set[ConsumerCategory] = (contexts flatMap (_.category)).toSet
    def scheduleCs: Set[String] = (contexts flatMap (_.scheduleC)).toSet

    def contexts: Vector[CategorizationContext] = raw.context map CategorizationContext
  }

}
