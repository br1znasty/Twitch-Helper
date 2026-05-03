describe('Checking the interaction with dasgboard', () => {
	const channelName = "T2X2"

	beforeEach(() => {
		cy.loginAs("main123@example.org", "123456");
		cy.visit("/#/home");
		cy.contains("Получение статистики").click()
	})

	it ('Try to get metrics without entering the channel name', () => {
		cy.contains("Канал").should("be.visible")

		cy.contains("Получить статистику").click()
		cy.wait(100)

		cy.contains("Введите название канала").should("be.visible")
	})

	it ('Try to get metrics without choosing any metrics - scenario 9', () => {
		cy.contains("Канал").should("be.visible")

		cy.get("#stats-channel").type(channelName, { delay: 200 })

		cy.get('input[type="checkbox"][value="status"]').uncheck()
		cy.get('input[type="checkbox"][value="viewers"]').uncheck()
		cy.get('input[type="checkbox"][value="display_name"]').uncheck()
		cy.get('input[type="checkbox"][value="description"]').uncheck()
		cy.get('input[type="checkbox"][value="followers"]').uncheck()

		cy.contains("Получить статистику").click()
		cy.wait(100)

		cy.contains("Ни одна метрика не выбрана").should("be.visible")
	})

	//it ('Try to get metrics for a non-existent channel - scenario 8', () => {
	//	cy.contains("Канал").should("be.visible")

	//	cy.get("#stats-channel").type("ASDWADASDWFFEUHGVIAW", {delay: 200})
	//})

	it ('Check that we can get metrics - scenario 7', () => {
		cy.contains("Канал").should("be.visible")

		cy.get("#stats-channel").type(channelName, { delay: 200 })

		cy.contains("Получить статистику").click()
		cy.wait(1000)

		cy.contains("Псевдоним стримера").should("be.visible")
	})
})