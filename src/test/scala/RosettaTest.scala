// useful definitions for testing Chisel modules inside Rosetta
object RosettaTest {
  // standard arguments to pass to chiselTest
  val stdArgs = Array("--genHarness", "--compile", "--test", "--backend", "c",
    "--targetDir", "build/test")
}
