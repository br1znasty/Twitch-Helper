describe('Checking the setting page', () => {
	const clientID = "7id3zvr39mupx516lk2pvodoqe4lr1"
	const clientSecret = "ryb0dixwqwtz2k63nkrrkiov3f2099"

	beforeEach(() => {
		cy.loginAs("main123@example.org", "123456");
		cy.visit("/#/home");
		cy.contains("Настройки").click();
	})

	it ('Check that the user cannot get the Access Token before it enter the Client ID and Client Secret', () => {
		cy.contains("Обновить токен принудительно").click();
		cy.wait(1500)
		cy.contains("Client ID is required. Please add your Twitch Client ID in settings.").should("be.visible")

		cy.get("#settings-client-id").type(clientID, { delay: 200 })

		cy.contains("Сохранить данные профиля").click();
		cy.wait(1500)
		cy.contains("Профиль сохранён").should("be.visible")

		cy.contains("Обновить токен принудительно").click();
		cy.wait(500)
		cy.contains("Client Secret is required. Please add your Twitch Client Secret in settings.").should("be.visible")

		cy.get("#settings-client-secret").type(clientSecret, { delay: 200 })

		cy.contains("Сохранить данные профиля").click();
		cy.wait(500)
		cy.contains("Профиль сохранён").should("be.visible")

		cy.contains("Обновить токен принудительно").click();
		cy.wait(500)
		cy.contains("Токен успешно обновлён").should("be.visible")
	})

	it ('Check that the Client ID and Client Secret was saved - scenario 11', () => {
		cy.get("#settings-client-id").invoke('val').should('eq', clientID)
		cy.get("#settings-client-secret").invoke('val').should('eq', clientSecret)
	})

	it ('Check that we can change the Access Token', () => {
		let initialToken = '';

		const getCurrentToken = () => {
			return cy.get("#settings-access-token").invoke('val')
		}

		cy.contains("Обновить токен принудительно").click()
		cy.wait(2000)

		getCurrentToken().then(token => {
			initialToken = token
			expect(initialToken).to.be.a('string').and.not.be.empty
		})

		for (let i = 0; i < 3; i++) {
			cy.wait(1500)
			cy.contains("Обновить токен принудительно").click()
			cy.wait(3000)
		
			getCurrentToken().then(newToken => {
				expect(newToken).not.to.equal(initialToken)
				initialToken = newToken
			})
		}
	
		cy.contains("Токен успешно обновлён").should("be.visible");
	})
})