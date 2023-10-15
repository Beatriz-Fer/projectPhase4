# base image
FROM node:18-alpine3.18

# working directory
WORKDIR /app

# installing Angular CLI
RUN npm install -g @angular/cli

# package.json and package-lock.json
COPY package*.json ./

# installing dependencies
RUN npm install

# copy the rest of code
COPY . .

# build app
RUN ng build

# port
EXPOSE 4200

# command to start application
CMD ["ng", "serve", "--host", "0.0.0.0"]