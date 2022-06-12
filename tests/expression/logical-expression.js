let T = true;
let F = false;
Test.expect(true, T || F);
Test.expect(true, F || T);
Test.expect(false, F || F);
Test.expect(true, T || T);

Test.expect(false, T && F);
Test.expect(false, F && T);
Test.expect(false, F && F);
Test.expect(true, T && T);

let t = "hello!";
let f = "";

Test.expect(t, t || f)
Test.expect(t, f || t)
Test.expect(f, f || f)
Test.expect(t, t || t)

Test.expect(f, t && f);
Test.expect(f, f && t);
Test.expect(f, f && f);
Test.expect(t, t && t);

let N = null;
let U = undefined;
let V = 500;
let A = 1000;

Test.expect(V, N ?? V);
Test.expect(V, U ?? V);
Test.expect(V, V ?? N);
Test.expect(V, V ?? U);

Test.expect(A, A ?? V);
Test.expect(V, V ?? A);