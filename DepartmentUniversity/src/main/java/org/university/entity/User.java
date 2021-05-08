package org.university.entity;

public class User {

    private final Integer id;
    private final String sex;
    private final String name;
    private final String email;
    private final String phone;
    private final String password;

    protected User(UserBuilder<? extends UserBuilder> heirBuilder) {
        this.id = heirBuilder.id;
        this.sex = heirBuilder.sex;
        this.name = heirBuilder.name;
        this.email = heirBuilder.email;
        this.phone = heirBuilder.phone;
        this.password = heirBuilder.password;
    }

    public Integer getId() {
        return id;
    }

    public String getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public static class UserBuilder<SELF extends UserBuilder<SELF>> {

        private Integer id;
        private String sex;
        private String name;
        private String email;
        private String phone;
        private String password;

        protected UserBuilder() {
        }

        public SELF withId(Integer id) {
            this.id = id;
            return self();
        }

        public SELF withSex(String sex) {
            this.sex = sex;
            return self();
        }

        public SELF withName(String name) {
            this.name = name;
            return self();
        }

        public SELF withEmail(String email) {
            this.email = email;
            return self();
        }

        public SELF withPhone(String phone) {
            this.phone = phone;
            return self();
        }

        public SELF withPassword(String password) {
            this.password = password;
            return self();
        }

        @SuppressWarnings("unchecked")
        public SELF self() {
            return (SELF) this;
        }

        public User build() {
            return new User(self());
        }
    }
}
