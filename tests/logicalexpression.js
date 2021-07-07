let T = true;
let F = false;
expect(true, T || F);
expect(true, F || T);
expect(false, F || F);
expect(true, T || T);

expect(false, T && F);
expect(false, F && T);
expect(false, F && F);
expect(true, T && T);

let t = "hello!";
let f = "";

expect(t, t || f)
expect(t, f || t)
expect(f, f || f)
expect(t, t || t)

expect(f, t && f);
expect(f, f && t);
expect(f, f && f);
expect(t, t && t);