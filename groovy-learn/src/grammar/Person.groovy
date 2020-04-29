package grammar

/**
 * @Description:
 * @author huangsenming
 * @date 2020/4/29 17:33
 */
class Person {
    private static int id;
    private String name;

    int getId() {
        return id
    }

    String getName() {
        return name
    }

    void setId(int id) {
        this.id = id
    }

    void setName(String name) {
        this.name = name
    }
}
