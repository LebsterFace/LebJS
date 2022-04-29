// this is a comment
// it should be ignored
function/* comments

can go anywhere
*/ name() {
    return/*1234*/5678
}

Test.expect/*
*/(/**/5678/*
*/, name/**/(/*
        */)/*
*/)