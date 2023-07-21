; Copies and pastes the content of the clipboard by typing it character by character. Useful when normal copy/paste doesn't work (e.g. remote desktop). Binded to CTRL+J
^j::
{
	clipboard := StrReplace(A_Clipboard, "`r`n", " ")
	clipboard := StrReplace(clipboard, "`t", " ")
	SendEvent clipboard
	return
}	