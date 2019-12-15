<html>
	<head>
	    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
        <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>

        <!-- DEPENDENCIES -->
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/jquery-ui/jquery-ui.min.js"></script>
        <script src="/webjars/jquery-validation/1.19.0/jquery.validate.min.js"></script>
        <script src="/webjars/jquery-mask-plugin/dist/jquery.mask.min.js"></script>
        <script src="/webjars/jquery.inputmask/3.3.7/min/jquery.inputmask.bundle.min.js"></script>

        <!-- GUI TOOLS -->
        <link rel="stylesheet" type="text/css" href="/webjars/pw-swift-guitools/1.0.3/xsd-gui-lib.css" />
        <link rel="stylesheet" type="text/css" href="/webjars/pw-swift-guitools/1.0.3/guitools-theme.css" />
        <script src="/webjars/pw-swift-guitools/1.0.3/xsd-gui-lib-main.js"></script>
        <script src="/webjars/pw-swift-guitools/1.0.3/xsd-gui-lib-validation.js"></script>
        <script src="/webjars/pw-swift-guitools/1.0.3/vendor/jquery.datetimepicker-1.3.3.full.min.js"></script>
        <link rel="stylesheet" type="text/css" href="/webjars/pw-swift-guitools/1.0.3/vendor/jquery.datetimepicker-1.3.3.css" />

        <!-- SAMPLE APP -->
        <script src="/js/forms.js"></script>
	    <script src="/js/${standard}-form.js"></script>
        <script src="/js/vendor/float-panel.js"></script>
		<link rel="stylesheet" type="text/css" href="/css/main.css"/>
    </head>
    <body>

        <div class="container">

            <h1>Message Form</h1>

            <div class="btn-group mb-3">
                <a href="/selection/${standard}" class="btn btn-outline-secondary">Cancel</a>
                <button id="validate" class="btn btn-outline-primary">Validate</button>
                <button formnovalidate="formnovalidate" class="btn btn-outline-primary" id="save">Save</button>
            </div>

            <form method="POST" class="message-form" action="/${standard}">
                ${form}
            </form>

        </div>
    </body>
</html>