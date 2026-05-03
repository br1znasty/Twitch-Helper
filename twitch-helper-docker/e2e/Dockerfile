FROM cypress/included:13.17.0

WORKDIR /e2e

COPY package*.json ./
RUN npm install

COPY cypress.config.js ./
COPY cypress ./cypress

CMD ["npm", "run", "cy:run:docker"]