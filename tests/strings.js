const project = "LebJS";
expect("LebJS", project.toString());
expect("LebJS", project.valueOf());
expect("Le", project.slice(0, 2));
expect("JS", project.slice(-2));
expect("L", project.charAt(0));
expect("SJbeL", project.reverse());
expect("foo", "   foo\n   ".trim())
expect("foo\n   ", "   foo\n   ".trimStart())
expect("   foo", "   foo\n   ".trimEnd())
expect("foo", "FOO".toLowerCase())
expect("FOO", "foo".toUpperCase())