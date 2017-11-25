## Chat-to-Discord

Sends ingame chat to a Discord channel

## Usage

1. Install the [latest `Chat-to-Discord-(version).jar`](https://github.com/Gjum/Chat-to-Discord/releases) (uses Forge)
1. Create a webhook in the config screen of one of your Discord channels (or ask an admin to create a webhook url for you)
    - Click the gear next to the channel name
    - Select `Webhooks` on the left
    - Click `Create Webhook`
    - Copy the `Webhook Url` at the bottom
1. Open the config screen:
    - From the start screen: `Mods`, select Chat-to-Discord on the left, then click `Config` at the bottom left
    - From the Escape menu: `Mods config`, select Chat-to-Discord on the left, then click `Config` at the bottom left
1. Paste the webhook url, change other options if you like
1. Click `Done` so your changes are saved (don't hit Escape)

## Development

Package the distributable `.jar`: `gradlew reobf`

You can then find it in `build/libs/`.
