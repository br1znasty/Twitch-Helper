describe('Checking frontend accessibility', () => {
	it('Go to the start page', () => {
		cy.visit('/')
		cy.wait(500)

		cy.contains('Twitch Helper')
		cy.wait(500)
	})
})