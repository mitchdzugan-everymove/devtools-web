package services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.store.FileDataStoreFactory

import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model._
import com.google.api.services.gmail.Gmail

import java.io._

object GmailService {
  /** Application name. */
  val APPLICATION_NAME = "Gmail API Java Quickstart"

  /** Directory to store user credentials for this application. */
  val DATA_STORE_DIR = new java.io.File(
    System.getProperty("user.home"), ".credentials/gmail-java-quickstart")

  /** Global instance of the FileDataStoreFactory. */
  var DATA_STORE_FACTORY : FileDataStoreFactory = null

  /** Global instance of the JSON factory. */
  val JSON_FACTORY =
    JacksonFactory.getDefaultInstance

  /** Global instance of the HTTP transport. */
  var HTTP_TRANSPORT : HttpTransport = null

  /** Global instance of the scopes required by this quickstart. */
  var SCOPES = GmailScopes.all()

  try {
    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR)
  } catch  {
    case t =>
      t.printStackTrace()
      System.exit(1);
  }

  /**
   * Creates an authorized Credential object.
   */
  def authorize() : Credential = {
    // Load client secrets.
    val in = new FileInputStream("/home/mitch/client_secret.json")
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in))

    // Build flow and trigger user authorization request.
    val flow =
      new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
        .setAccessType("offline")
        .build()
    val credential = new AuthorizationCodeInstalledApp(
      flow, new LocalServerReceiver()).authorize("user")
    System.out.println(
      "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath)
    credential
  }

  /**
   * Build and return an authorized Gmail client service.
   */
  def getGmailService : Gmail  = {
    val credential = authorize()
    new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
      .setApplicationName(APPLICATION_NAME)
      .build()
  }

  val service = getGmailService
  val user = "me"

  def stringGet : String = {
    val a = service.users().messages().list(user).execute().getMessages().get(0)
    val b = service.users().messages().get(user, a.getId).execute()
    val c = service.users().threads().get(user, a.getThreadId).execute()
    println(b.toPrettyString)
    println(b.getPayload.getHeaders)
    b.getPayload.getBody.decodeData().map(_.toChar).mkString
  }

}