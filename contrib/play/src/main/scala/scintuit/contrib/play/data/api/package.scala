package scintuit.contrib.play.data

package object api {

  object all extends AllFormats

  trait AllFormats extends account.AccountFormats with categorization.CategorizationFormats with error.ErrorFormats
  with institution.InstitutionFormats with login.LoginFormats with position.PositionFormats
  with security.SecurityFormats with transaction.TransactionFormats

}
