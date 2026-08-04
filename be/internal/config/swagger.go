package config

import (
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/swagger"
)

// SetupSwagger returns a fiber handler for Swagger UI with custom dark mode CSS
func SetupSwagger() fiber.Handler {
	return swagger.New(swagger.Config{
		CustomStyle: `
			body {
				background: #1e1e1e !important;
				color: #e0e0e0 !important;
			}
			.swagger-ui {
				background: #1e1e1e !important;
			}
			.swagger-ui .info .title,
			.swagger-ui .info p,
			.swagger-ui .info a,
			.swagger-ui .opblock .opblock-summary-path,
			.swagger-ui .opblock .opblock-summary-description,
			.swagger-ui .parameter__name,
			.swagger-ui .parameter__type,
			.swagger-ui table thead tr th,
			.swagger-ui table thead tr td,
			.swagger-ui .response-col_status,
			.swagger-ui .response-col_description,
			.swagger-ui .tab li,
			.swagger-ui .model-title,
			.swagger-ui .model,
			.swagger-ui .prop-type,
			.swagger-ui .prop-format,
			.swagger-ui .opblock-tag,
			.swagger-ui .opblock-tag small,
			.swagger-ui .opblock .opblock-summary-operation-id,
			.swagger-ui .dialog-ux .modal-ux-header h3,
			.swagger-ui .dialog-ux .modal-ux-content h4,
			.swagger-ui .dialog-ux .modal-ux-content p,
			.swagger-ui .dialog-ux .modal-ux-content label,
			.swagger-ui .auth-wrapper .auth-container .auth-title,
			.swagger-ui .auth-wrapper .auth-container .auth-title code,
			.swagger-ui .dialog-ux .modal-ux-content h6,
			.swagger-ui .opblock .opblock-section-header h4,
			.swagger-ui .opblock .opblock-section-header label {
				color: #ffffff !important;
			}
			.swagger-ui .dialog-ux .modal-ux-header .close-modal svg,
			.swagger-ui svg {
				fill: #ffffff !important;
			}
			.swagger-ui .scheme-container,
			.swagger-ui .opblock .opblock-section-header,
			.swagger-ui .parameters-col_description input,
			.swagger-ui .parameters-col_description select,
			.swagger-ui .dialog-ux .modal-ux,
			.swagger-ui .model-box {
				background: #2d2d2d !important;
				box-shadow: none !important;
			}
			.swagger-ui .opblock {
				background: #1e1e1e !important;
				border: 1px solid #444 !important;
			}
			.swagger-ui .opblock .opblock-summary {
				border-bottom: 1px solid #444 !important;
			}
			.swagger-ui .btn {
				color: #ffffff !important;
				border-color: #777 !important;
				background: #333 !important;
			}
			.swagger-ui input,
			.swagger-ui select,
			.swagger-ui textarea {
				background: #333 !important;
				color: #fff !important;
				border: 1px solid #555 !important;
			}
			.swagger-ui .topbar {
				background: #111 !important;
			}
		`,
	})
}
