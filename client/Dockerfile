FROM node:13.8

COPY . /client

WORKDIR /client
RUN yarn install

USER nobody

ENTRYPOINT ["yarn","start"]
