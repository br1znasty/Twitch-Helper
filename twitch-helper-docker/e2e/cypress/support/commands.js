Cypress.Commands.add('loginAs', (email, password) => {
	cy.session([email, password], () => {
		cy.visit("/");
		cy.contains("Вход").click()
		cy.get("#email").type(email, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Войти").click()
	})
})