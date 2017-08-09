SBT ?= sbt
SBT_FLAGS ?= -Dsbt.log.noformat=true
CC = g++
TOP ?= .
BUILD_DIR ?= $(TOP)/build
BUILD_DIR_VERILOG := $(BUILD_DIR)/hw/verilog
BUILD_DIR_HWCPP := $(BUILD_DIR)/hw/cpp_emu
BUILD_DIR_HWDRV := $(BUILD_DIR)/hw/driver

hw_verilog:
	$(SBT) $(SBT_FLAGS) "runMain rosetta.ChiselMain --backend v --targetDir $(BUILD_DIR_VERILOG)"

hw_cpp:
	$(SBT) $(SBT_FLAGS) "runMain rosetta.ChiselMain --backend c --targetDir $(BUILD_DIR_HWCPP)"

hw_driver:
	mkdir -p "$(BUILD_DIR_HWDRV)"; $(SBT) $(SBT_FLAGS) "runMain rosetta.DriverMain $(BUILD_DIR_HWDRV)"

.PHONY: hw_verilog
