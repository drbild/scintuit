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

package scintuit.util

import java.security.{PrivateKey, Signature}
import java.util.{Base64, UUID}

import com.github.nscala_time.time.Imports._

object saml {
  type UserId = String

  case class SamlIssuer(id: String) {
    override def toString: String = id
  }

  def signedAssertion(key: PrivateKey, issuer: SamlIssuer, userId: UserId): String =
    SamlAssertion(key, issuer, userId).signed

  object SamlAssertion {
    def apply(key: PrivateKey, issuer: SamlIssuer, userId: UserId): SamlAssertion =
      new SamlAssertion(key, issuer, userId)
  }

  class SamlAssertion(key: PrivateKey, issuer: SamlIssuer, userId: UserId) {
    val now = DateTime.now(DateTimeZone.UTC)
    val fiveMinutesAgo = now - 5.minutes
    val tenMinutesFromNow = now + 10.minutes

    val referenceId = UUID.randomUUID.toString.replace("-", "")

    private def sign(content: String) = {
      val rsa = Signature.getInstance("SHA1WithRSA")
      rsa.initSign(key)
      rsa.update(content.getBytes)
      Base64.getEncoder.encodeToString(rsa.sign)
    }

    // @formatter:off
    private val assertion = s"""
    |<saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion" ID="_$referenceId" IssueInstant="$now" Version="2.0">
      |<saml2:Issuer>$issuer</saml2:Issuer>
      |<saml2:Subject>
        |<saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">$userId</saml2:NameID>
        |<saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer"></saml2:SubjectConfirmation>
      |</saml2:Subject>
      |<saml2:Conditions NotBefore="$fiveMinutesAgo" NotOnOrAfter="$tenMinutesFromNow">
        |<saml2:AudienceRestriction>
          |<saml2:Audience>$issuer</saml2:Audience>
        |</saml2:AudienceRestriction>
      |</saml2:Conditions>
      |<saml2:AuthnStatement AuthnInstant="$now" SessionIndex="_$referenceId">
        |<saml2:AuthnContext>
          |<saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified</saml2:AuthnContextClassRef>
        |</saml2:AuthnContext>
      |</saml2:AuthnStatement>
    |</saml2:Assertion>""".stripMargin.replaceAll("\n","")
    // @formatter:on

    private val digest = {
      val sha1 = java.security.MessageDigest.getInstance("SHA-1")
      Base64.getEncoder.encodeToString(sha1.digest(assertion.getBytes))
    }

    lazy val signed = {
      // @formatter:off
      val signedInfo = s"""
      |<ds:SignedInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
        |<ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:CanonicalizationMethod>
        |<ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"></ds:SignatureMethod>
        |<ds:Reference URI="#_$referenceId">
          |<ds:Transforms>
            |<ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"></ds:Transform>
            |<ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:Transform>
          |</ds:Transforms>
          |<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"></ds:DigestMethod>
          |<ds:DigestValue>$digest</ds:DigestValue>
        |</ds:Reference>
      |</ds:SignedInfo>""".stripMargin.replaceAll("\n", "")
      // @formatter:on

      // @formatter:off
      val signature = s"""
         |<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
           |<ds:SignedInfo>
             |<ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
             |<ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
             |<ds:Reference URI="#_$referenceId">
               |<ds:Transforms>
                 |<ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
                 |<ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
               |</ds:Transforms>
               |<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
               |<ds:DigestValue>$digest</ds:DigestValue>
             |</ds:Reference>
           |</ds:SignedInfo>
           |<ds:SignatureValue>${sign(signedInfo)}</ds:SignatureValue>
         |</ds:Signature>""".stripMargin.replaceAll("\n", "")
      // @formatter:on

      Base64.getEncoder.encodeToString(
        assertion.replace("saml2:Issuer><saml2:Subject", s"saml2:Issuer>${signature}<saml2:Subject").getBytes
      )
    }
  }

}
