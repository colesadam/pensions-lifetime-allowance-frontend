/*
 * Copyright 2017 HM Revenue & Customs
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

package config

import play.api.Play._
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val betaFeedbackUrl: String
  val betaFeedbackUnauthenticatedUrl: String
  val analyticsToken: String
  val analyticsHost: String
  val ssoUrl: Option[String]
  val citizenAuthHost: Option[String]
  val contactFormServiceIdentifier: String
  val contactFrontendPartialBaseUrl: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val excludeCopeTab: Boolean
  val identityVerification: Boolean
  val confirmFPUrl: String
  val ipStartUrl: String
  val ip14StartUrl: String
  val existingProtectionsUrl: String
  val notAuthorisedRedirectUrl: String
  val verifySignIn = s"$citizenAuthHost/ida/login"
  val ivUpliftUrl: String
  val twoFactorUrl: String
  val ggSignInUrl: String
  val ptaFrontendUrl: String
}

object FrontendAppConfig extends AppConfig with ServicesConfig {
  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing key: $key"))

  private val contactFrontendService = baseUrl("contact-frontend")
  private val contactHost = configuration.getString(s"contact-frontend.host").getOrElse("")
  private val baseUrl = "protect-your-lifetime-allowance"

  override lazy val betaFeedbackUrl = s"$baseUrl/feedback"
  override lazy val betaFeedbackUnauthenticatedUrl = betaFeedbackUrl
  override lazy val analyticsToken: String = configuration.getString(s"google-analytics.token").getOrElse("")
  override lazy val analyticsHost: String = configuration.getString(s"google-analytics.host").getOrElse("auto")
  override lazy val ssoUrl: Option[String] = configuration.getString(s"portal.ssoUrl")

  override val contactFormServiceIdentifier = "PLA"
  override lazy val contactFrontendPartialBaseUrl = s"$contactFrontendService"
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override val excludeCopeTab: Boolean = configuration.getBoolean(s"microservice.services.exclusions.copetab").getOrElse(true)
  override val identityVerification: Boolean = configuration.getBoolean("microservice.services.features.identityVerification").getOrElse(false)

  override lazy val citizenAuthHost = configuration.getString("citizen-auth.host")
  override lazy val confirmFPUrl = configuration.getString("confirmFP.url").getOrElse("")
  override lazy val ipStartUrl = configuration.getString("ipStart.url").getOrElse("")
  override lazy val ip14StartUrl = configuration.getString("ip14Start.url").getOrElse("")
  override lazy val existingProtectionsUrl = configuration.getString("existingProtections.url").getOrElse("")
  override lazy val ptaFrontendUrl = configuration.getString("pta-frontend.url").getOrElse("")

  override lazy val notAuthorisedRedirectUrl = configuration.getString("not-authorised-callback.url").getOrElse("")
  override val ivUpliftUrl: String = configuration.getString(s"identity-verification-uplift.host").getOrElse("")
  override val ggSignInUrl: String = configuration.getString(s"government-gateway-sign-in.host").getOrElse("")
  override val twoFactorUrl: String = configuration.getString(s"two-factor.host").getOrElse("")

}
