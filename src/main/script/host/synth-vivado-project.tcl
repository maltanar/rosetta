if {$argc != 1} {
  puts "Expected: <project_to_synthesize>"
  exit
}

open_project [lindex $argv 0]
# synthesis
launch_runs synth_1 -jobs 4
wait_on_run synth_1
# place and route
launch_runs impl_1 -to_step write_bitstream -jobs 4
wait_on_run impl_1
