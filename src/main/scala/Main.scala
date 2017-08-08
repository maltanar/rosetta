package Hello

import Chisel._
import fpgatidbits.PlatformWrapper.PYNQWrapper
import fpgatidbits.Testbenches.TestRegOps

object RosettaTemplate {
  def main(args: Array[String]): Unit = {
    val chiselArgs = Array("--backend","v","--targetDir", "build")
    chiselMain(chiselArgs, () => Module(new PYNQWrapper(p => new TestRegOps(p))))
  }
}
