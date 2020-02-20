FROM node:13.8

COPY . /client

USER nobody
RUN chown -R nobody /client

WORKDIR /client
RUN yarn install

ENTRYPOINT ["yarn","start"]
