[
    {
        "name": "login",
        "commands": [
            {
                "command": "open",
                "target": "http://localhost:8080",
                "value": "",
                "skip": 560,
                "index": 560,
                "result": "done",
                "type": "command"
            },
            {
                "command": "clickForAjaxResponse",
                "target": "id=login",
                "value": "",
                "skip": 0,
                "index": 629,
                "result": "done",
                "type": "command"
            },
            {
                "command": "waitForAjaxResponse",
                "target": "3000",
                "value": "Login",
                "skip": 0,
                "index": 629,
                "result": "done",
                "type": "command"
            },
            {
                "command": "waitForElementPresent",
                "target": "id=test",
                "value": "",
                "result": "done",
                "type": "command"
            }
        ],
        "counter": -1
    },
    {
        "name": "loginToMviewer",
        "commands": [
            {
                "command": "open",
                "target": "http://localhost:8080/index.html",
                "value": "",
                "type": "command"
            },
            {
                "command": "clickForAjaxResponse",
                "target": "//button[@id='login']",
                "value": "",
                "type": "command"
            }
        ],
        "counter": -1
    },
    {
        "name": "waitWithAjaxForElement",
        "commands": [
            {
                "command": "waitForAjaxResponse",
                "target": "30000",
                "value": "",
                "type": "command"
            },
            {
                "command": "waitForElementPresent",
                "target": "id=test",
                "value": "",
                "type": "command"
            }
        ],
        "counter": -1
    }
]