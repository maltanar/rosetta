# Rosetta
Rosetta is a project template to rapidly deploy Chisel accelerators on the Xilinx PYNQ platform. It uses the PlatformWrapper components from the fpga-tidbits framework for easy memory mapped register file management.

## Requirements
1. A working Chisel2 and sbt setup.
2. Xilinx Vivado 2016.4 (make sure vivado is in PATH)
3. A PYNQ board with network access

## Quickstart
1. Clone this repository and cd into it
2. Run make pynq, this may take several minutes. This will create a deployment folder under build/rosetta
3. Run make rsync to copy generated files to the PYNQ board. You may have to edit the BOARD_URI variable in the Makefile to get this working.
4. Open a PYNQ terminal via ssh, and cd into ~/rosetta
5. Run ./compile_sw.sh to compile the sample application
6. Run sudo ./load_bitfile.sh to configure the FPGA with the bitfile
7. Run the sample application with sudo ./app
8. Enter two integers -- you should see their sum printed correctly, as computed by the FPGA accelerator.

## Under the Hood
1. Have a look at the hardware description under the src/main/scala
2. Have a look at the example application under src/main/cpp/app
3. Have a look at what the different Makefile targets generate
