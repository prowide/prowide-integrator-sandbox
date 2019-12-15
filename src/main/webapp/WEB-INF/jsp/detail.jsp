<html>
	<head>
	    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
        <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/jquery-ui/jquery-ui.min.js"></script>
        <link rel="stylesheet" type="text/css" href="/webjars/pw-swift-guitools/1.0.3/xsd-gui-lib.css" />
        <link rel="stylesheet" type="text/css" href="/webjars/pw-swift-guitools/1.0.3/guitools-theme.css" />
        <script src="/webjars/pw-swift-guitools/1.0.3/xsd-gui-lib-main.js"></script>
        <script src="/js/${standard}-form.js"></script>
        <link rel="stylesheet" type="text/css" href="/css/main.css"/>
	</head>
    <body>
        <div class="container">

            <h1>Message Detail</h1>

            <div class="btn-group mb-3">
                <a href="/" class="btn btn-outline-secondary">Back</a>
                <a href="/repair/${id}" class="btn btn-outline-primary">Repair</a>
            </div>

            <div class="message-detail">
                ${detail}
            </div>

        </div>
    </body>
</html>