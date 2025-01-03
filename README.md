# **LebJS**
A JavaScript engine written in Java

![Demonstration](demo.gif)

## Common Usage

### Run a file
```sh
lebjs [file]
```

### Run the tests
```sh
lebjs -t [dir]
```
`[dir]` defaults to `./tests`

### Enter the REPL
```sh
lebjs
```

## REPL commands
```
.help                      Display this message
.clear                     Clear the screen
.inspect [expression]      Deep print the result of [expression]
```

## Flags
- `-v`, `--verbose` - Don't hide stack traces
- `--parse-only` - Ignore test failures from parsing
- `--ignore-not-impl` - Ignore test failures from unimplemented features
- `--no-buffer` - Do not buffer test outputs
- `--hide-passing` - Only output skipped / failing tests (ignored if `--no-buffer` specified)
- `--disable-prompt` - Disable the `> ` prompt in the REPL
- `--harness [value]` - Test harness. Valid options: `ladybird`
- `-t`, `--test` - Run tests
- `--gif` - Enable GIF rendering mode (No error handling, no prompt, print delimiter after execution, print AST)