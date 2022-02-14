const project = "LebJS";
expect("LebJS", project.toString());
expect("LebJS", project.valueOf());
expect("Le", project.slice(0, 2));
expect("JS", project.slice(-2));
expect("L", project.charAt(0));
expect("SJbeL", project.reverse());