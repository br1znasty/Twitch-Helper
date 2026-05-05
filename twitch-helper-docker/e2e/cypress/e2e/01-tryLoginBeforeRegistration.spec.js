describe('Check sing in and sing up', () => {
	it('Try to sing in, sing up, sing in', () => {
		const email = "main123@example.org"
		const username = "qwe"
		const password = "123456"

		cy.visit("/")
		cy.wait(500)

		cy.contains("Вход").click()
		cy.wait(500)

		cy.get("#email").type(email, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Войти").click();
		cy.wait(1000)

		cy.contains("Invalid email or password").should("be.visible")
		cy.wait(500)

		cy.contains("Назад").click()
		cy.wait(500)

		cy.contains("Регистрация").click()
		cy.wait(500)

		cy.get("#username").type(username, { delay: 200 })
		cy.get("#email").type(email, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Зарегистрироваться").click()
		cy.wait(500)

		cy.contains("Выход").should("be.visible")
		cy.wait(500)
	})
})
