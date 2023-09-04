package hr.eduwalk.common

import android.text.Editable
import android.text.TextWatcher

interface DefaultTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {} // no-op

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {} // no-op

    override fun afterTextChanged(s: Editable?) {} // no-op
}
