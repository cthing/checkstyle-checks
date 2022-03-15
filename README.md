# ![C Thing Software](https://www.cthing.com/branding/CThingSoftware-57x60.png "C Thing Software") checkstyle-checks
Library of custom checkers for Checkstyle.

### Usage
See [Integrate Your Check](https://checkstyle.sourceforge.io/writingchecks.html#Integrate_your_Check) and
[Packages](https://checkstyle.sourceforge.io/config.html#Packages) in the Checkstyle documentation for instructions
on including custom checks in your Checkstyle configuration file.

### Checker Reference

#### LogDeclaration
Checks that the declaration of an SLF4J log object conforms to a standard format. The declaration must have the format:
```
private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);
```
Where `MyClass` must be replaced with the class enclosing the declaration. Annotations are allowed on the declaration and
will be ignored by this check.

To use the check:
```
<module name="TreeWalker">
    ...
    <module name="LogDeclaration"/>
    ...
</module>    
```

#### SpringDaoAnnotation
Checks that a DAO implementation class for use with the Spring framework contains the appropriate annotations. Public
classes ending with {@code DaoImpl} are considered DAO implementation classes. The expected annotations are:

* Classes must be marked `@Repository`
* Classes must be marked `@Transactional(readOnly = true)`
* All public `insert*` methods must be marked `@Transactional(readOnly = false)`
* All public `update*` methods must be marked `@Transactional(readOnly = false)`
* All public `delete*` methods must be marked `@Transactional(readOnly = false)`

Properties:
* `includePattern` (String) - Regular expression for the DAO filenames to include in checking. Default is `^.*DaoImpl$`
* `excludePattern` (String) - Regular expression for the DAO filenames to exclude from checking. Default is `^Abstract.+$`

To use the check:
```
<module name="TreeWalker">
    ...
    <module name="SpringDaoAnnotation"/>
    ...
</module>    
```

#### TestMethodDeclaration
Checks the declaration of a unit test method annotated with `@Test`. The method must be declared `public` and have a
`void` return type.

To use the check:
```
<module name="TreeWalker">
    ...
    <module name="TestMethodDeclaration"/>
    ...
</module>    
```

### Building
The libray is compiled for Java 11.

Gradle is used to build the library:
```
./gradlew build
```
