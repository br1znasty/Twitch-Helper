describe('Checking logout', () => {
	beforeEach(() => {
		cy.loginAs("main123@example.org", "123456");
		cy.visit("/#/home");
	})

	it ('Check that the user remains logged in when the page is refreshed - scenario 5', () => {
		cy.contains("Выход").should("be.visible")
		cy.wait(500)

		cy.reload()

		cy.contains("Выход").should("be.visible")
		cy.wait(500)
	})

	it ('Check logout - scenario 6', () => {
		cy.contains("Выход").should("be.visible")
		cy.contains("Выход").click()
		cy.wait(500)

		cy.contains("Вход").should("be.visible")
		cy.wait(500)

		cy.reload()

		cy.contains("Вход").should("be.visible")
		cy.wait(500)

		cy.go(-1)

		cy.contains("Вход").should("be.visible")
		cy.wait(500)
	})
})