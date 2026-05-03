describe('Check if Fronted is up', () => {
	it('visit front', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains('Twitch Helper')
		cy.wait(500)
	})
})