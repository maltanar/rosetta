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
5. Run sudo ./load_bitfile.sh to configure the FPGA with the bitfile
6. Try pressing the the buttons (BTN0..3) on the PYNQ to control the LEDs (LD0..3)
7. Run ./compile_sw.sh to compile the sample application
8. Run the sample application with sudo ./app
9. Enter two integers -- you should see their sum printed correctly, as computed by the FPGA accelerator.

## Under the Hood
1. Have a look at the hardware description under the src/main/scala -- the accelerator definition is in Accelerator.scala, the "entry point" for code generation is in Main.scala, and the infrastructure (where the magic happens) is in Rosetta.scala
2. Have a look at the example application under src/main/cpp/app -- note that it uses the auto-generated register driver to access the hardware signals. The register driver will be generated in build/hw/driver
3. Have a look at what the different Makefile targets generate inside the build/ folder. You can also try launching Vivado with the make launch_vivado_gui target after the project has been generated.
