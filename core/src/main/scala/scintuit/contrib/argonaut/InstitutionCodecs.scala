package scintuit.contrib.argonaut

import argonaut.Argonaut._
import argonaut.CodecJson
import scintuit.data.{Institution, InstitutionSummary, Key}

trait InstitutionCodecs extends CommonCodecs {
  implicit def InstitutionSummaryCodecJson: CodecJson[InstitutionSummary] =
    casecodec5(InstitutionSummary.apply, InstitutionSummary.unapply)(
      "institutionId",
      "institutionName",
      "homeUrl",
      "phoneNumber",
      "virtual"
    )

  implicit def InstitutionCodecJson: CodecJson[Institution] = casecodec10(Institution.apply, Institution.unapply)(
    "institutionId",
    "institutionName",
    "homeUrl",
    "phoneNumber",
    "virtual",
    "address",
    "emailAddress",
    "specialText",
    "currencyCode",
    "keys"
  )

  implicit def KeyCodecJson: CodecJson[Key] = casecodec10(Key.apply, Key.unapply)(
    "name",
    "val",
    "status",
    "valueLengthMin",
    "valueLengthMax",
    "displayFlag",
    "displayOrder",
    "mask",
    "instructions",
    "description"
  )
}
