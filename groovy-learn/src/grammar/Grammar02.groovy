package grammar
import grammar.Person;

/**
 * @Description:
 * @author huangsenming
 * @date 2020/4/29 17:21
 */
class Grammar02 {
    public static void main(String[] args) {
        def person = Person.find { it.id == 123 }
        def name = person?.name
        assert name == null
    }
}
