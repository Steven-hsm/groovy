####  Unicode转义序列 

 对于键盘上不存在的字符，可以使用unicode转义序列:一个反斜杠，后跟'u'，然后是4个十六进制数字。  例如，欧元货币符号可以表示为: 

```groovy
'The Euro currency symbol: \u20AC'
```

####  双引号字符串 

 双引号字符串是由双引号包围的一系列字符: 

```groovy
"a double-quoted string"
```

>  如果双引号字符串没有内插表达式就是是普通的java.lang.string ,如果存在插值表达式，则是 groovy.lang.GString 实例。 
>
>  要转义双引号，可以使用反斜杠字符:" a double quote: \""。 

#####  字符串插值 

 除了单引号和三重单引号的字符串外，任何Groovy表达式都可以在所有字符串中插入。插值表达式是在字符串求值时用字符串的值替换字符串中的占位符的行为。占位符表达式被${}包围。花括号可以省略为明确的虚线表达式，即我们可以在这些情况下使用一个$前缀。如果将GString传递给带有字符串的方法，占位符内的表达式值将被计算为其字符串表示形式(通过调用该表达式上的toString())，结果字符串将被传递给该方法。 

 在这里，我们有一个字符串与一个占位符引用一个局部变量: 

```groovy
def name = 'Guillaume' // a plain string
def greeting = "Hello ${name}"

assert greeting.toString() == 'Hello Guillaume'
```

 任何Groovy表达式都是有效的，正如我们在这个例子中看到的算术表达式: 

```groovy
def sum = "The sum of 2 and 3 equals ${2 + 3}"
assert sum.toString() == 'The sum of 2 and 3 equals 5'
```

 不仅在${}占位符之间允许使用表达式，语句也是如此。但是，语句的值是null。因此，如果在那个占位符中插入了几个语句，那么最后一个语句应该以某种方式返回要插入的有意义的值。例如，“1和2的和等于${def a = 1;def b = 2;a + b}，其工作与预期一致，但是一个好的实践通常是坚持使用GString占位符中的简单表达式。 

 除了${}占位符之外，我们还可以使用一个单独的$符号作为点表达式的前缀: 

```groovy
def person = [name: 'Guillaume', age: 36]
assert "$person.name is $person.age years old" == 'Guillaume is 36 years old'
```

 但是只有点乘形式表达式例如a.b, a.b.c等等，都是有效的。包含括号的表达式(如方法调用、闭包的大括号、不属于属性表达式或算术运算符的点)是无效的。下面给出一个数字的变量定义: 

```groovy
def number = 3.14
```

 下面的语句将抛出一个 groovy.lang.MissingPropertyException ,因为groovy认为你试图使用一个number的toString方法,但是它并不存在.

```groovy
shouldFail(MissingPropertyException) {
    println "$number.toString()"
}
```

> 您可以将“$number.toString()”理解为解析器将其解释为“${number.toString}()”。 

 类似地，如果表达式是模稜两可的，你需要保留花括号: 

```groovy
String thing = 'treasure'
assert 'The x-coordinate of the treasure is represented by treasure.x' ==
    "The x-coordinate of the $thing is represented by $thing.x"   // <= Not allowed: ambiguous!!
assert 'The x-coordinate of the treasure is represented by treasure.x' ==
        "The x-coordinate of the $thing is represented by ${thing}.x"  // <= Curly braces required
```

 如果你需要转义GString中的$或${}占位符，使它们看起来没有插值表达式，你只需要使用\反斜杠字符来转义美元符号: 

```groovy
assert '$5' == "\$5"
assert '${name}' == "\${name}"
```

##### 插值闭包表达式的特殊情况 

 到目前为止，我们已经看到我们可以在${}占位符中插入任意表达式，但是对于闭包表达式有一个特殊的情况和符号。当占位符包含一个箭头${→}时，表达式实际上是一个闭包表达式—您可以将它看作一个闭包，在它前面有一个美元前缀: 

```groovy
def sParameterLessClosure = "1 + 2 == ${-> 3}" 
assert sParameterLessClosure == '1 + 2 == 3'

def sOneParamClosure = "1 + 2 == ${ w -> w << 3}" 
assert sOneParamClosure == '1 + 2 == 3'
```

从外观上看，它似乎是一种更冗长的定义表达式的方法，但是闭包与纯粹的表达式相比有一个有趣的优点:延迟求值。

让我们考虑以下例子:

```groovy
def number = 1 
def eagerGString = "value == ${number}"
def lazyGString = "value == ${ -> number }"

assert eagerGString == "value == 1" 
assert lazyGString ==  "value == 1" 

number = 2 
assert eagerGString == "value == 1" 
assert lazyGString ==  "value == 2" 
```

>  包含多个参数的嵌入式闭包表达式将在运行时生成异常。只允许带有零个或一个参数的闭包。 

#####  与Java的互操作性 

 当一个方法(无论用Java还是Groovy实现)需要 java.lang.String 时。但我们传递了一个 groovy.lang.GString 实例，GString的toString()方法被自动透明地调用。 

```groovy
String takeString(String message) {         
    assert message instanceof String        
    return message
}

def message = "The message is ${'hello'}"   
assert message instanceof GString           

def result = takeString(message)            
assert result instanceof String
assert result == 'The message is hello'
```

#####  GString和String 的hashcode

 虽然可以使用内插字符串代替普通的Java字符串，但它们与字符串的区别在于:它们的hashcode不同。普通Java字符串是不可变的，而GString的结果字符串表示可以根据它的内插值而变化。即使对于相同的结果字符串，GStrings和字符串也没有相同的hashCode。 

```groo
assert "one: ${1}".hashCode() != "one: 1".hashCode()
```

 应该避免使用GString和具有不同hashCode值的字符串，特别是当我们试图用字符串而不是GString检索关联的值时。 

```groovy
def key = "a"
def m = ["${key}": "letter ${key}"]     
assert m["a"] == null       
```

#### 三重双引号字符串

 三重双引号字符串的行为类似于双引号字符串，但它们是多行字符串，类似于三重单引号字符串。 

```groovy
def name = 'Groovy'
def template = """
    Dear Mr ${name},

    You're the winner of the lottery!

    Yours sincerly,

    Dave
"""

assert template.toString().contains('Groovy')
```

>  双引号和单引号都不需要在三重双引号的字符串中转义。 

#### 斜线字符串 

 除了常用的带引号字符串外，Groovy还提供了斜线字符串，使用/作为开始和结束分隔符。斜线字符串对于定义正则表达式和模式特别有用，因为不需要转义反斜线。 斜线字符串的例子

```groovy
def fooPattern = /.*foo.*/
assert fooPattern == '.*foo.*'
```

 只有前斜杠需要用反斜杠转义: 

```groovy
def escapeSlash = /The character \/ is a forward slash/
assert escapeSlash == 'The character / is a forward slash'
```

 斜线字符串可以是多行: 

```groovy
def multilineSlashy = /one
    two
    three/
assert multilineSlashy.contains('\n')
```

 Slashy字符串可以被认为是定义GString的另一种方式，但是有不同的转义规则。因此，它们支持插值: 

```groovy
def color = 'blue'
def interpolatedSlashy = /a ${color} car/

assert interpolatedSlashy == 'a blue car'
```

##### 特殊情况

 一个空的斜杠字符串不能用双斜杠表示，因为它被Groovy解析器理解为行注释。这就是为什么下面的断言实际上不能编译，因为它看起来像一个 ''

```gr
assert '' == //
```

 由于斜线字符串的设计主要是为了简化regexp，所以gstring中的一些错误(如$()或$5)可以用于斜线字符串。 

 请记住，不需要转义反斜杠。另一种考虑方法是，实际上不支持转义。斜线字符串/\t/不包含制表符，而是一个反斜杠，后跟字符“t”。仅允许对斜杠字符进行转义，即/\/folder/将是一个包含'/folder'的斜杠字符串。斜杠转义的结果是斜杠字符串不能以反斜杠结束。否则它将退出斜线字符串终止符。你可以使用一个特殊的技巧，/以斜杠${'\'}/结尾。但是最好避免在这种情况下使用斜线。 

#### $/字符串

 $斜线字符串是用开头$/和结尾$分隔的多行gstring。转义字符是美元符号，它可以转义另一个美元或正斜杠。但是$和前向斜杠都不需要转义，除非要转义一个字符串子序列的$，该子序列的起始位置类似于GString占位符序列，或者需要转义一个序列，该序列的起始位置类似于一个闭合的$斜杠字符串分隔符。 

这里有一个例子:

```groo
def name = "Guillaume"
def date = "April, 1st"

def dollarSlashy = $/
    Hello $name,
    today we're ${date}.

    $ dollar sign
    $$ escaped dollar sign
    \ backslash
    / forward slash
    $/ escaped forward slash
    $$$/ escaped opening dollar slashy
    $/$$ escaped closing dollar slashy
/$

assert [
    'Guillaume',
    'April, 1st',
    '$ dollar sign',
    '$ escaped dollar sign',
    '\\ backslash',
    '/ forward slash',
    '/ escaped forward slash',
    '$/ escaped opening dollar slashy',
    '/$ escaped closing dollar slashy'
].every { dollarSlashy.contains(it) }
```

 它的创建是为了克服斜线字符串转义规则的一些限制。当它的转义规则适合您的字符串内容时使用它(通常如果它有一些您不想转义的斜线)。 

##### 字符串总结