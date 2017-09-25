package rosetta

import Chisel._
import sys.process._

object Settings {
  // Rosetta will use myInstFxn to instantiate your accelerator
  // edit below to change which accelerator will be instantiated
  //val myInstFxn = {() => new TestRegOps()}
  //val myInstFxn = {() => new TestAccumulateVector(4)}
  //val myInstFxn = {() => new BRAMExample(1024)}
  val myInstFxn = {() => new DRAMExample()}
}

// call this object's main method to generate Chisel Verilog and C++ emulation
// output products. all cmdline arguments are passed straight to Chisel.
object ChiselMain {
  def main(args: Array[String]): Unit = {
    chiselMain(args, () => Module(new RosettaWrapper(Settings.myInstFxn)))
  }
}

// call this object's main method to generate the register driver for your
// accelerator. expects the following command line arguments, in order:
// 1. path to output directory for generated files
// 2. path to Rosetta drivers
object DriverMain {
  // utility functions to copy files inside Scala
  def fileCopy(from: String, to: String) = {
    s"cp -f $from $to" !
  }

  def fileCopyBulk(fromDir: String, toDir: String, fileNames: Seq[String]) = {
    for(f <- fileNames)
      fileCopy(s"$fromDir/$f", s"$toDir/$f")
  }

  def main(args: Array[String]): Unit = {
    val outDir = args(0)
    val drvSrcDir = args(1)
    // instantiate the wrapper accelerator
    val myModule = Module(new RosettaWrapper(Settings.myInstFxn))
    // generate the register driver
    myModule.generateRegDriver(outDir)
    // copy additional driver files
    fileCopyBulk(drvSrcDir, outDir, myModule.platformDriverFiles)
  }
}
