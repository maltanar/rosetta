package rosetta

import Chisel._
import fpgatidbits.PlatformWrapper.PYNQWrapper
import fpgatidbits.Testbenches.TestRegOps

object Settings {
  // Rosetta will use myInstFxn to instantiate your accelerator
  // edit below to change which accelerator will be instantiated
  val myInstFxn = {p => new TestRegOps(p)}
}

// call this object's main method to generate Chisel Verilog and C++ emulation
// output products. all cmdline arguments are passed straight to Chisel.
object ChiselMain {
  def main(args: Array[String]): Unit = {
    chiselMain(args, () => Module(new PYNQWrapper(Settings.myInstFxn)))
  }
}

// call this object's main method to generate the register driver for your
// accelerator. specify the target directory as the first argument.
object DriverMain {
  def main(args: Array[String]): Unit = {
    val myModule = Module(new PYNQWrapper(Settings.myInstFxn))
    myModule.generateRegDriver(args(0))
  }
}
