Cypress.Commands.add('loginAs', (email, password) => {
	cy.session([email, password], () => {
		cy.visit("/");
		cy.contains("Вход").click()
		cy.get("#email").type(email, { delay: 50 })
		cy.get("#password").type(password, { delay: 50 })
		cy.contains("Войти").click()
	})
})