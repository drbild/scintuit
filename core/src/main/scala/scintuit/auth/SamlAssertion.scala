package scintuit.auth

import java.security.{PrivateKey, Signature}
import java.util.{Base64, UUID}

import com.github.nscala_time.time.Imports._
import scintuit.Customer

import scalaz.Reader

case class SamlAssertion(body: String)

object SamlAssertion {

  def apply(key: PrivateKey, provider: SamlProvider, customer: Customer): SamlAssertion = {
    val now = DateTime.now(DateTimeZone.UTC)
    val ref = UUID.randomUUID.toString.replace("-", "")
    SamlAssertion(build(Params(now, ref, key, provider, customer)))
  }

  type Assertion = String
  type Digest = String
  type SignedInfo = String
  type SignatureValue = String
  type Signature = String

  private case class Params(
    instant: DateTime,
    referenceId: String,
    key: PrivateKey,
    issuer: SamlProvider,
    name: Customer
  )

  private def build =
    for {
      a <- assertion
      d = digest(a)
      i <- signedInfo(d)
      v <- signatureValue(i)
      s <- signature(d, v)
    } yield signedAssertion(a, s)

  private def digest(assertion: Assertion): Digest = {
    val sha1 = java.security.MessageDigest.getInstance("SHA-1")
    Base64.getEncoder.encodeToString(sha1.digest(assertion.getBytes))
  }

  private def signedAssertion(assertion: Assertion, signature: Signature) =
    Base64.getEncoder.encodeToString(
      assertion.replace("saml2:Issuer><saml2:Subject", s"saml2:Issuer>${signature}<saml2:Subject").getBytes
    )

  private def signatureValue(signedInfo: SignedInfo) = Reader { (params: Params) =>
    val rsa = Signature.getInstance("SHA1WithRSA")
    rsa.initSign(params.key)
    rsa.update(signedInfo.getBytes)
    Base64.getEncoder.encodeToString(rsa.sign)
  }

  private def assertion = Reader { (params: Params) =>
    s"""<saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion" ID="_${params.referenceId}" IssueInstant="${params.instant}" Version="2.0"><saml2:Issuer>${params.issuer}</saml2:Issuer><saml2:Subject><saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">${params.name}</saml2:NameID><saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer"></saml2:SubjectConfirmation></saml2:Subject><saml2:Conditions NotBefore="${params.instant - 5.minutes}" NotOnOrAfter="${params.instant + 10.minutes}"><saml2:AudienceRestriction><saml2:Audience>${params.issuer}</saml2:Audience></saml2:AudienceRestriction></saml2:Conditions><saml2:AuthnStatement AuthnInstant="${params.instant}" SessionIndex="_${params.referenceId}"><saml2:AuthnContext><saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified</saml2:AuthnContextClassRef></saml2:AuthnContext></saml2:AuthnStatement></saml2:Assertion>"""
  }

  private def signedInfo(digest: Digest) = Reader { (params: Params) =>
    s"""<ds:SignedInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:CanonicalizationMethod><ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"></ds:SignatureMethod><ds:Reference URI="#_${params.referenceId}"><ds:Transforms><ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"></ds:Transform><ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"></ds:DigestMethod><ds:DigestValue>${digest}</ds:DigestValue></ds:Reference></ds:SignedInfo>"""
  }

  private def signature(digest: Digest, signatureValue: SignatureValue) = Reader { (params: Params) =>
    s"""<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/><ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/><ds:Reference URI="#_${params.referenceId}"><ds:Transforms><ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/><ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/></ds:Transforms><ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/><ds:DigestValue>${digest}</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>${signatureValue}</ds:SignatureValue></ds:Signature>"""
  }
}
