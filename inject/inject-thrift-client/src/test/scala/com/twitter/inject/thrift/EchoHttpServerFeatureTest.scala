package com.twitter.inject.thrift

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.test.{EmbeddedHttpServer, HttpTest}
import com.twitter.inject.server.EmbeddedTwitterServer
import com.twitter.inject.thrift.integration.http_server.EchoHttpServer
import com.twitter.inject.thrift.integration.thrift_server.EchoThriftServer

class EchoHttpServerFeatureTest extends HttpTest {

  val thriftServer = new EmbeddedTwitterServer(
    twitterServer = new EchoThriftServer)

  val httpServer = new EmbeddedHttpServer(
    twitterServer = new EchoHttpServer,
    extraArgs = Seq(
      resolverMap("thrift-echo-service" -> thriftServer.thriftHostAndPort)))

  "EchoHttpServer" should {
    "Echo 3 times" in {
      pending //Will re-enable once new ThriftClientModule RB merges
      httpServer.httpPost(
        path = "/config?timesToEcho=2",
        postBody = "",
        andExpect = Ok,
        withBody = "2")

      httpServer.httpPost(
        path = "/config?timesToEcho=3",
        postBody = "",
        andExpect = Ok,
        withBody = "3")

      httpServer.httpGet(
        path = "/echo?msg=Bob",
        andExpect = Ok,
        withBody = "BobBobBob")

      httpServer.printStats()
      httpServer.close()
      thriftServer.close()
    }
  }
}
