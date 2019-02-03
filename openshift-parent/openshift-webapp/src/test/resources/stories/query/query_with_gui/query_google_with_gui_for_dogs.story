Query Google with GUI for dogs

Meta:
@issue DAD-40
@tag product:search

Narrative:
In order to get attachments associated with a document
As a valid user
I want to query for attachments by document id

Scenario: Should list items related to a specified keyword
Given I want to buy a wool scarf
When I want to search for items containing 'wool'
Then I want to only see items related to 'wool'

Scenario: Should be able to filter search results by item type
Given I want to have searched for items containing 'wool'
When I want to filter results by type 'Handmade'
Then I want to should only see items containing 'wool' of type 'Handmade'
