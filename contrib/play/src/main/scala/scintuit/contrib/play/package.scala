package scintuit.contrib

package object play {

  trait AllFormats extends AccountFormats with CommonFormats with InstitutionFormats with LoginFormats with
  PositionFormats with SecurityInfoFormats with TransactionFormats

  val account = AccountFormats
  val common = CommonFormats
  val institution = InstitutionFormats
  val login = LoginFormats
  val position = InstitutionFormats
  val securityInfo = SecurityInfoFormats
  val transactions = TransactionFormats

  object all extends AllFormats
}
