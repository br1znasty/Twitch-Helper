describe('Checking the auth', () => {
	const occupiedEmail = "main123@example.org"
	const occupiedUsername = "qwe"
	const password = "123456"
	const email = "mail@example.com"
	const username = "kirill"

	it('Try to sing up with occupied email - scenario 2', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains("Регистрация").click()
		cy.wait(500)

		cy.get("#username").type(username, { delay: 200 })
		cy.get("#email").type(occupiedEmail, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Зарегистрироваться").click()
		cy.wait(1000)

		cy.contains("Email is already in use").should("be.visible")
		cy.wait(500)
	})

	it ('Try to sing up with occupied username', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains("Регистрация").click()
		cy.wait(500)

		cy.get("#username").type(occupiedUsername, { delay: 200 })
		cy.get("#email").type(email, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Зарегистрироваться").click()
		cy.wait(1000)

		cy.contains("Username is already taken").should("be.visible")
		cy.wait(500)
	})

	it ('Try to sing in with invalid password - scenario 4', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains("Вход").click()
		cy.wait(500)

		cy.get("#email").type(occupiedEmail, { delay: 200 })
		cy.get("#password").type("1234567", { delay: 200 })
		cy.contains("Войти").click()
		cy.wait(1000)

		cy.contains("Invalid email or password").should("be.visible")
		cy.wait(500)
	})

	it ('Correct sing up - scenario 1', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains("Регистрация").click()
		cy.wait(500)

		cy.get("#username").type(username, { delay: 200 })
		cy.get("#email").type(email, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Зарегистрироваться").click()
		cy.wait(1000)

		cy.contains("Выход").should("be.visible")
		cy.wait(500)
	})

	it ('Correct sing in - scenario 3', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains("Вход").click()
		cy.wait(500)

		cy.get("#email").type(email, { delay: 200 })
		cy.get("#password").type(password, { delay: 200 })
		cy.contains("Войти").click()
		cy.wait(1000)

		cy.contains("Выход").should("be.visible")
		cy.wait(500)
	})
})