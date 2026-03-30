# Create UfoPort — Porting Lib Coexistence Branch

this branch has the porting lib coexistence fix. our porting_lib_ufo can now run alongside the official porting lib that other mods use (like critters and companions, sophisticated mods, etc).

all overlapping interface methods are prefixed with `port_lib_ufo$` so both libs can inject into vanilla classes without crashing.

no more `breaks` or `provides` — both just load side by side.
