package rosetta

import Chisel._

class Shift(array_size: Int, data_width: Int) extends RosettaAccelerator {
    def Adder(a: UInt, b: UInt):UInt = {
        a + b
    }
    
    val numMemPorts = 0
    val io = new RosettaAcceleratorIF(numMemPorts) {
        val nums_in = Vec.fill(array_size){UInt(INPUT, data_width)}

        // Need a good way to store weights
        val weights = Vec.fill(array_size){UInt(INPUT, data_width)}
        
        val data_out = Vec.fill(array_size){UInt(OUTPUT, data_width)}
    }

    for(i <- 0 to array_size-1){
        io.data_out(i) := Adder(io.nums_in(i), io.weights(i))
    }

}

class ShiftTests(c: Shift) extends Tester(c) {
    poke(c.io.nums_in, Array[BigInt](1,2,3,4))
    poke(c.io.weights, Array[BigInt](40,30,20,10))

    peek(c.io.data_out)
}
