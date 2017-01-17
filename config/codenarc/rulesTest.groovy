ruleset {
    ruleset('file:config/codenarc/rules.groovy') {
        exclude 'JUnitPublicProperty' // Fields annotated with @org.junit.Rule violate this rule
        exclude 'NonFinalPublicField' // Fields annotated with @org.junit.Rule violate this rule
        exclude 'PublicInstanceField' // Fields annotated with @org.junit.Rule violate this rule
        exclude 'UnusedPrivateField' // Fields annotated with @org.junit.Rule violate this rule
        exclude 'MethodName' // More natural sentences in test cases
        exclude 'SystemErrPrint' // Needed to segment the integration test
        exclude 'JUnitPublicNonTestMethod' // Using Spock
        exclude 'NoDef' // Required for acceptance test DSL loose coupling
        exclude 'ClosureAsLastMethodParameter' // Required by Spock expectation to match method call by closure
    }
}
