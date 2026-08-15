package bot

type UpdateBotPromptRequest struct {
	AgentPrompt string `json:"agent_prompt" example:"Kamu adalah asisten e-commerce AI yang ramah."`
	AgentAPI    string `json:"agent_api" example:"https://api.example.com"`
	WaNumber    string `json:"wa_number" example:"08123456789"`
}
