package com.example.viewsreview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.viewsreview.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var expressionString = ""
    private var resultString = ""
    private var openParenthesisCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onOperandClicked()
        onOperatorClicked()

    }

    private fun onOperandClicked() {

        binding.btn0.setOnClickListener {
            handleSingleZero()
            appendToResult('0')
        }

        binding.btn1.setOnClickListener {
            handleSingleZero()
            appendToResult('1')
        }

        binding.btn2.setOnClickListener {
            handleSingleZero()
            appendToResult('2')
        }

        binding.btn3.setOnClickListener {
            handleSingleZero()
            appendToResult('3')
        }

        binding.btn4.setOnClickListener {
            handleSingleZero()
            appendToResult('4')
        }

        binding.btn5.setOnClickListener {
            handleSingleZero()
            appendToResult('5')
        }

        binding.btn6.setOnClickListener {
            handleSingleZero()
            appendToResult('6')
        }

        binding.btn7.setOnClickListener {
            handleSingleZero()
            appendToResult('7')
        }

        binding.btn8.setOnClickListener {
            handleSingleZero()
            appendToResult('8')
        }

        binding.btn9.setOnClickListener {
            handleSingleZero()
            appendToResult('9')
        }

        binding.btnFloatingPoint.setOnClickListener {
            if (resultString.isEmpty()) {
                appendToResult('0')
                appendToResult('.')
            } else if (countFloatingPoint() == 0) {
                appendToResult('.')
            }
        }

    }

    private fun onOperatorClicked() {

        binding.btnBackspace.setOnClickListener {
            if (checkLastChar(resultString, '(')) {
                openParenthesisCount--
            } else if (checkLastChar(resultString, ')')) {
                openParenthesisCount++
            }
            deleteLastCharInResult()
            binding.txtResultSign.text = ""
        }

        binding.btnClear.setOnClickListener {
            clearExpression()
            clearResult()

            openParenthesisCount == 0
            binding.txtResultSign.text = ""
        }

        binding.btnParenthesisOpen.setOnClickListener {
            if (resultString.isEmpty() || checkLastOperator(resultString) || checkLastChar(
                    resultString,
                    '('
                )
            ) {
                if (!checkLastChar(expressionString, ')')) {
                    appendToResult('(')
                    openParenthesisCount++
                }
            }
        }

        binding.btnParenthesisClose.setOnClickListener {
            if (!checkLastChar(
                    resultString,
                    '('
                ) && !checkLastOperator(resultString) && openParenthesisCount > 0
            ) {
                handleFloatingPoint()

                appendToResult(')')
                openParenthesisCount--

                if (openParenthesisCount == 0) {
                    resultToExpression()
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            addOperator('+')
        }

        binding.btnSubtract.setOnClickListener {
            addOperator('-')
        }

        binding.btnMultiple.setOnClickListener {
            addOperator('*')
        }

        binding.btnDivide.setOnClickListener {
            addOperator('/')
        }

        binding.btnEquals.setOnClickListener {
            try {

                if (openParenthesisCount == 0) {

                    addOperator(' ') //in order to add the remaining to expression
                    deleteLastCharInExpression() //deleting the extra space in expression

                    val expression = ExpressionBuilder(expressionString).build()
                    val result: Double = expression.evaluate()
                    val longResult: Long = result.toLong()

                    //the user still has to see the expression so I wont pass it to txtExpression
                    //in case of new operation, this will be vacant
                    expressionString = ""

                    resultString = if (result == longResult.toDouble()) {
                        longResult.toString()
                    } else {
                        result.toString()
                    }

                    binding.txtResultSign.text = "= "
                    binding.txtResult.text = resultString

                } else {
                    showToast("The Expression is wrong!")
                }

            } catch (e: Exception) {

                clearExpression()
                clearResult()

                openParenthesisCount == 0

                showToast("There was an Error!")

            }
        }

    }

    private fun appendToExpression(char: Char) {
        expressionString += char
        binding.txtExpression.text = expressionString
    }

    private fun appendToResult(char: Char) {
        resultString += char
        binding.txtResult.text = resultString
    }

    //check and if true, it deletes the single zero (to be replaced by the new number)
    private fun handleSingleZero() {
        if (resultString == "0") {
            resultString = ""
        }
    }

    private fun deleteLastCharInResult() {
        if (resultString.isNotEmpty()) {
            val stringBuilder = StringBuilder(resultString)
            resultString = stringBuilder.deleteAt(resultString.length - 1).toString()
            binding.txtResult.text = resultString
        }
    }

    private fun deleteLastCharInExpression() {
        if (expressionString.isNotEmpty()) {
            val stringBuilder = StringBuilder(expressionString)
            expressionString = stringBuilder.deleteAt(expressionString.length - 1).toString()
            binding.txtExpression.text = expressionString
        }
    }

    private fun checkLastOperator(string: String): Boolean {
        return if (string.isEmpty()) {
            false
        } else if (string.last() == '+' || string.last() == '-' || string.last() == '*' || string.last() == '/') {
            true
        } else {
            false
        }
    }

    private fun resultToExpression() {
        expressionString += resultString
        binding.txtExpression.text = expressionString

        clearResult()
    }

    private fun addOperator(operator: Char) {
        if (openParenthesisCount == 0) { //until every open parenthesis has a close one, the expression remains on resultString

            when {
                resultString.isNotEmpty() -> {
                    handleFloatingPoint()
                    resultToExpression()
                    appendToExpression(operator)
                }

                checkLastOperator(expressionString) -> {
                    deleteLastCharInExpression()
                    appendToExpression(operator)
                }

                expressionString.isNotEmpty() -> {
                    appendToExpression(operator)
                }
            }

        } else { //when there is no single open parenthesis

            if (checkLastOperator(resultString)) {
                deleteLastCharInResult()
                appendToResult(operator)
            } else {
                handleFloatingPoint()
                appendToResult(operator)
            }

        }

        binding.txtResultSign.text = ""

        //end of add operator
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun countFloatingPoint(): Int {
        var floatingPointCount = 0
        for (char in resultString) {
            if (char == '.') {
                floatingPointCount++
            }
        }
        return floatingPointCount
    }

    private fun checkLastChar(string: String, char: Char): Boolean {
        return string.isNotEmpty() && string.last() == char
    }

    private fun clearExpression() {
        expressionString = ""
        binding.txtExpression.text = expressionString
    }

    private fun clearResult() {
        resultString = ""
        binding.txtResult.text = resultString
    }

    private fun handleFloatingPoint() {
        if (checkLastChar(resultString, '.')) {
            deleteLastCharInResult()
        }
    }

}